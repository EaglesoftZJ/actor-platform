//
//  CommonServiceDelegate.h
//  OAMobile
//
//  Created by daijinping on 13-10-14.
//  Copyright (c) 2013年 eagleSoft. All rights reserved.
//

#import <Foundation/Foundation.h>

@class CommonService;

@protocol CommonServiceDelegate <NSObject>

@optional
- (void)serviceStart:(CommonService *)svc;

@optional
- (void)serviceEnd:(CommonService *)svc;

@required
- (void)serviceSuccess:(CommonService *)svc object:(id)obj;

@required
- (void)serviceFail:(CommonService *)svc info:(NSString *)info;

@end
