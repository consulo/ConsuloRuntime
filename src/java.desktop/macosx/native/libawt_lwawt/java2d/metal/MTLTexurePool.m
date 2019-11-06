#import "MTLTexturePool.h"
#import "Trace.h"

@implementation MTLTexturePoolItem

@synthesize texture;
@synthesize isBusy;

- (id) initWithTexture:(id<MTLTexture>)tex {
    self = [super init];
    if (self == nil) return self;
    self.texture = tex;
    isBusy = NO;
    return self;
}
@end

@implementation MTLTexturePool

@synthesize device;
@synthesize pool;

- (id) initWithDevice:(id<MTLDevice>)dev {
    self = [super init];
    if (self == nil) return self;

    self.device = dev;
    self.pool = [NSMutableArray arrayWithCapacity:10];
    return self;
}

// NOTE: called from RQ-thread (on blit operations)
- (id<MTLTexture>) getTexture:(int)width height:(int)height format:(MTLPixelFormat)format {
    @synchronized (self) {
        @autoreleasepool {
            // 1. find free item
            // TODO: optimize search, use Map<(w,h,pf), TexPoolItem>
            const int count = [self.pool count];
            int minDeltaArea = -1;
            int minDeltaAreaIndex = -1;
            for (int c = 0; c < count; ++c) {
                MTLTexturePoolItem *tpi = [self.pool objectAtIndex:c];
                if (tpi == nil || tpi.isBusy || tpi.texture.pixelFormat != format) { // TODO: use swizzle when formats are not equal
                    continue;
                }
                if (tpi.texture.width < width || tpi.texture.height < height) {
                    continue;
                }
                const int deltaArea = tpi.texture.width*tpi.texture.height - width*height;
                if (deltaArea == 0) {
                    minDeltaAreaIndex = c;
                    break;
                }

                if (minDeltaArea < 0 || deltaArea < minDeltaArea) {
                    minDeltaAreaIndex = c;
                    minDeltaArea = deltaArea;
                }
            }

            if (minDeltaAreaIndex >= 0) {
                MTLTexturePoolItem *tpi = [self.pool objectAtIndex:minDeltaAreaIndex];
                tpi.isBusy = YES;
                return tpi.texture;
            }

            // 2. create
            MTLTextureDescriptor *textureDescriptor = [MTLTextureDescriptor texture2DDescriptorWithPixelFormat:format width:width height:height mipmapped:NO];
            id <MTLTexture> tex = [[self.device newTextureWithDescriptor:textureDescriptor] autorelease];
            MTLTexturePoolItem *tpi = [[[MTLTexturePoolItem alloc] initWithTexture:tex] autorelease];
            [self.pool addObject:tpi];
            J2dTraceLn4(J2D_TRACE_VERBOSE, "MTLTexturePool: created pool item: tex=%p, w=%d h=%d, pf=%d", tex, width, height, format);
            return tpi.texture;
        }
    }
};

// NOTE: called from completion-handler (pooled thread)
- (void) markTextureFree:(id<MTLTexture>)texture {
    // TODO: optimize search, use Map<(w,h,pf), TexPoolItem>
    @synchronized (self) {
        const int count = [self.pool count];
        for (int c = 0; c < count; ++c) {
            MTLTexturePoolItem * tpi = [self.pool objectAtIndex:c];
            if (tpi == nil)
                continue;
            if (tpi.texture == texture) {
                tpi.isBusy = NO;
                return;
            }
        }
        J2dTraceLn1(J2D_TRACE_ERROR, "MTLTexturePool: can't find item with texture %p", texture);
    }
}

// NOTE: called from completion-handler (pooled thread)
- (void) markAllTexturesFree {
    @synchronized (self) {
        const int count = [self.pool count];
        for (int c = 0; c < count; ++c) {
            MTLTexturePoolItem *tpi = [self.pool objectAtIndex:c];
            if (tpi == nil)
                continue;
            tpi.isBusy = NO;
        }
    }
}


@end