//
//  PhoneImageService.h
//  ActorSDK
//
//  Created by dingjinming on 2017/11/21.
//  Copyright © 2017年 Steve Kite. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "WebService.h"
#import "JSON.h"
#import "CommonService.h"
#import "CommonServiceDelegate.h"
#import <ASIHTTPRequest/ASIHTTPRequest.h>
@interface PhoneImageService : CommonService<ASIHTTPRequestDelegate>
{
    id<CommonServiceDelegate> delegate;
}

@property(retain, nonatomic) id<CommonServiceDelegate> delegate;

- (void)getLaunchImage;

@end
