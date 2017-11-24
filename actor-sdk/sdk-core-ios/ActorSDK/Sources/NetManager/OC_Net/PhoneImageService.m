//
//  PhoneImageService.m
//  ActorSDK
//
//  Created by dingjinming on 2017/11/21.
//  Copyright © 2017年 Steve Kite. All rights reserved.
//

#import "PhoneImageService.h"
#import <ActorSDK/ActorSDK-Swift.h>
@implementation PhoneImageService

@synthesize delegate;

- (void)getLaunchImage{
    NSString *webserviceURL = @"http://61.175.100.14:8012/ActorServices-Maven/services/ActorService?wsdl";
    
    NSMutableArray *params = [NSMutableArray array];NSString *version;
    
    [params addObject:@"version"];
    if (![[NSUserDefaults standardUserDefaults] objectForKey:@"version"])
    {version = @"1";}
    else{version = [[NSUserDefaults standardUserDefaults] objectForKey:@"version"];}
    [params addObject:version];
    
    ASIHTTPRequest *req = [WebService getJAVAASISOAP11Request:webserviceURL
                                             xmlNameSpace:@"http://eaglesoft"
                                           webServiceName:@"phone_image"
                                             wsParameters:params];
    [req setDelegate:self];
    [req startAsynchronous];
}

- (void)request:(ASIHTTPRequest *)request didReceiveBytes:(long long)bytes
{
}

- (void)requestFinished:(ASIHTTPRequest *)request
{
    NSString *responseString = [request responseString];
    NSString *jsonString = [WebService getJSONStringFromSOAP:responseString];
    NSDictionary *jsonData = [jsonString JSONValue];
    [delegate serviceSuccess:self object:jsonData];
}

- (void)requestFailed:(ASIHTTPRequest *)request
{
    [delegate serviceFail:self info:[ActorSDK sharedActor].ERR_HTTP_REQUEST];
}
@end
