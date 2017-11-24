//
//  WebService.h
//  ActorSDK
//
//  Created by dingjinming on 2017/11/16.
//  Copyright © 2017年 Steve Kite. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <ASIHTTPRequest/ASIHTTPRequest.h>
@interface WebService : NSObject
@property (nonatomic,strong) NSString *url;
@property (nonatomic,strong) NSString *xmlns;
@property (nonatomic,strong) NSString *webServiceName;
@property (nonatomic,strong) NSArray *argList;
@property (nonatomic,strong) NSString *callbackName;


+ (ASIHTTPRequest *)getASISOAP11Request:(NSString *) wsurl
                           xmlNameSpace:(NSString *) xmlNS
                         webServiceName:(NSString *) wsName
                           wsParameters:(NSMutableArray *) wsParas;

+ (ASIHTTPRequest *)getJAVAASISOAP11Request:(NSString *) wsurl
                               xmlNameSpace:(NSString *) xmlNS
                             webServiceName:(NSString *) wsName
                               wsParameters:(NSMutableArray *) wsParas;
+ (NSString *)getJSONStringFromSOAP:(NSString *)soap;

@end
