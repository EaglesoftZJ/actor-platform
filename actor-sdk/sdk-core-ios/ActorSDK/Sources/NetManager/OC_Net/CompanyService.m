//
//  CompanyService.m
//  ActorSDK
//
//  Created by dingjinming on 2017/11/17.
//  Copyright © 2017年 Steve Kite. All rights reserved.
//

#import "CompanyService.h"
#import <ActorSDK/ActorSDK-Swift.h>
@implementation CompanyService

@synthesize delegate;

- (void)chooseCompany
{
    NSString *webServiceURL = [ActorSDK sharedActor].webserviceIP;
    
    NSMutableArray *params = [NSMutableArray array];
    
    [params addObject:@"k"];
    [params addObject:[ActorSDK sharedActor].param_K];
    
    ASIHTTPRequest *req = [WebService getASISOAP11Request:webServiceURL
                                             xmlNameSpace:[ActorSDK sharedActor].supportHomepage
                                           webServiceName:@"GetAllUserFullData"
                                             wsParameters:params];
    [req setDelegate:self];
    [req startAsynchronous];
//    [delegate serviceStart:self];
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
//     [delegate serviceEnd:self];
}

- (void)requestFailed:(ASIHTTPRequest *)request
{
    [delegate serviceFail:self info:[ActorSDK sharedActor].ERR_HTTP_REQUEST];
//    [delegate serviceEnd:self];
}
@end
