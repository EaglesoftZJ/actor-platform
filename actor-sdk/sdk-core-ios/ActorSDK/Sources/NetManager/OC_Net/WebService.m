//
//  WebService.m
//  ActorSDK
//
//  Created by dingjinming on 2017/11/16.
//  Copyright © 2017年 Steve Kite. All rights reserved.
//

#import "WebService.h"
#import <GDataXMLNode2/GDataXMLNode.h>

@implementation WebService
+ (ASIHTTPRequest *)getASISOAP11Request:(NSString *) wsurl
                           xmlNameSpace:(NSString *) xmlNS
                         webServiceName:(NSString *) wsName
                           wsParameters:(NSMutableArray *) wsParas
{
    //1、初始化SOAP消息体
    NSString * soapMsgBody1 = [[NSString alloc] initWithFormat:
                               @"<?xml version=\"1.0\" encoding=\"utf-8\"?>\n"
                               "<soap:Envelope xmlns:soap=\"http://www.w3.org/2003/05/soap-envelope\" xmlns:eag=\"http://www.eaglesoft.cn\">\n"
                               "<soap:Body>\n"
                               "<eag:%@>\n", wsName];
    NSString * soapMsgBody2 = [[NSString alloc] initWithFormat:
                               @"</eag:%@>\n"
                               "</soap:Body>\n"
                               "</soap:Envelope>", wsName];
    //2、生成SOAP调用参数
    NSString *soapParas = @"";
    if (![wsParas isEqual:nil])
    {
        int i = 0;
        for (i = 0; i < [wsParas count]; i = i + 2)
        {
            soapParas = [soapParas stringByAppendingFormat:@"<eag:%@>%@</eag:%@>\n",
                         [wsParas objectAtIndex:i],
                         [wsParas objectAtIndex:i+1],
                         [wsParas objectAtIndex:i]];
        }
    }
    //3、生成SOAP消息
    NSString * soapMsg = [soapMsgBody1 stringByAppendingFormat:@"%@%@", soapParas, soapMsgBody2];
    //请求发送到的路径
    NSURL * url = [NSURL URLWithString:wsurl];
    ASIHTTPRequest * theRequest = [ASIHTTPRequest requestWithURL:url];
    [theRequest setCachePolicy:ASIDoNotReadFromCacheCachePolicy]; //ASIDoNotReadFromCacheCachePolicy
    
    NSString *msgLength = [NSString stringWithFormat:@"%lu", (unsigned long)[soapMsg length]];
    //以下对请求信息添加属性前四句是必有的，第五句是soap信息。
    [theRequest addRequestHeader:@"Content-Type" value:@"text/xml; charset=utf-8"];
    [theRequest addRequestHeader:@"SOAPAction" value:[NSString stringWithFormat:@"%@/%@", xmlNS, wsName]];
    
    [theRequest addRequestHeader:@"Content-Length" value:msgLength];
    [theRequest setRequestMethod:@"POST"];
    [theRequest appendPostData:[soapMsg dataUsingEncoding:NSUTF8StringEncoding]];
    [theRequest setDefaultResponseEncoding:NSUTF8StringEncoding];
    
    return theRequest;
}
//#胡春杰的webservice
+ (ASIHTTPRequest *)getJAVAASISOAP11Request:(NSString *) wsurl
                           xmlNameSpace:(NSString *) xmlNS
                         webServiceName:(NSString *) wsName
                           wsParameters:(NSMutableArray *) wsParas
{
    //1、初始化SOAP消息体
    NSString * soapMsgBody1 = [[NSString alloc] initWithFormat:
                               @"<?xml version=\"1.0\" encoding=\"utf-8\"?>\n"
                               "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:eag=\"http://eaglesoft\">\n"
                               "<soap:Body>\n"
                               "<eag:%@>\n", wsName];
    NSString * soapMsgBody2 = [[NSString alloc] initWithFormat:
                               @"</eag:%@>\n"
                               "</soap:Body>\n"
                               "</soap:Envelope>", wsName];
    //2、生成SOAP调用参数
    NSString *soapParas = @"";
    if (![wsParas isEqual:nil])
    {
        int i = 0;
        for (i = 0; i < [wsParas count]; i = i + 2)
        {
            soapParas = [soapParas stringByAppendingFormat:@"<eag:%@>%@</eag:%@>\n",
                         [wsParas objectAtIndex:i],
                         [wsParas objectAtIndex:i+1],
                         [wsParas objectAtIndex:i]];
        }
    }
    //3、生成SOAP消息
    NSString * soapMsg = [soapMsgBody1 stringByAppendingFormat:@"%@%@", soapParas, soapMsgBody2];
    //请求发送到的路径
    NSURL * url = [NSURL URLWithString:wsurl];
    ASIHTTPRequest * theRequest = [ASIHTTPRequest requestWithURL:url];
    [theRequest setCachePolicy:ASIDoNotReadFromCacheCachePolicy]; //ASIDoNotReadFromCacheCachePolicy
    
    NSString *msgLength = [NSString stringWithFormat:@"%lu", (unsigned long)[soapMsg length]];
    //以下对请求信息添加属性前四句是必有的，第五句是soap信息。
    [theRequest addRequestHeader:@"Content-Type" value:@"text/xml; charset=utf-8"];
    [theRequest addRequestHeader:@"SOAPAction" value:[NSString stringWithFormat:@"%@/%@", xmlNS, wsName]];
    
    [theRequest addRequestHeader:@"Content-Length" value:msgLength];
    [theRequest setRequestMethod:@"POST"];
    [theRequest appendPostData:[soapMsg dataUsingEncoding:NSUTF8StringEncoding]];
    [theRequest setDefaultResponseEncoding:NSUTF8StringEncoding];
    
    return theRequest;
}
+ (NSString *)getJSONStringFromSOAP:(NSString *)soap
{
    GDataXMLDocument *doc = [[GDataXMLDocument alloc] initWithXMLString:soap options:0 error:0];
    
    GDataXMLElement *envelope = [doc rootElement];
    GDataXMLElement *body = [envelope children][0];
    GDataXMLElement *response = [body children][0];
    GDataXMLElement *result = [response children][0];
    
    NSString *jsonString = [result stringValue];
    
    return jsonString;
}
@end
