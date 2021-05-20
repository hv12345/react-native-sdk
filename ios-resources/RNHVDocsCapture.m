//
//  RNHVDocsCapture.m
//  HyperSnapSDK
//
//  Copyright © 2018 HyperVerge. All rights reserved.

#import "RNHVDocsCapture.h"
#import "AppDelegate.h"

@implementation RNHVDocsCapture

RCT_EXPORT_MODULE()

BOOL shouldShowInstructionPage = false;
HVDocConfig * hvDocConfig;

HVDocConfig* getDocConfig(){
  if (hvDocConfig == NULL){
    hvDocConfig = [[HVDocConfig alloc] init];
  }
  return hvDocConfig;
}

RCT_EXPORT_METHOD(setOCRAPIDetails:(NSString *)endpoint documentSide:(DocumentSide)documentSide
                  params:(NSDictionary<NSString *,id> * _Nullable)params headers:(NSDictionary<NSString *,id> * _Nullable)headers) {
  [getDocConfig() setOCRAPIDetails:endpoint documentSide:documentSide params:params headers:headers];
}

RCT_EXPORT_METHOD(setDocCaptureTitle:(NSString *)titleText){
  [getDocConfig().textConfig setDocCaptureTitle:titleText];
}
RCT_EXPORT_METHOD(setDocCaptureDescription:(NSString *)description){
  [getDocConfig().textConfig setDocCaptureDescription:description];
}
RCT_EXPORT_METHOD(setDocCaptureSubText:(NSString *)subText){
  [getDocConfig().textConfig setDocCaptureSubText:subText];
}
RCT_EXPORT_METHOD(setDocReviewTitle:(NSString *)docReviewTitle){
  [getDocConfig().textConfig setDocReviewTitle:docReviewTitle];
}
RCT_EXPORT_METHOD(setDocReviewDescription:(NSString *)docReviewDescription){
  [getDocConfig().textConfig setDocReviewDescription:docReviewDescription];
}

RCT_EXPORT_METHOD(setAspectRatio:(double)aspectRatio){
  [getDocConfig() setAspectRatio:aspectRatio];
}

RCT_EXPORT_METHOD(setDocumentType:(DocumentType)documentType){
  [getDocConfig() setDocumentType:documentType];
}

RCT_EXPORT_METHOD(setShouldAddPadding:(DocumentType)shouldAdd){
  [getDocConfig() setShouldAddPadding:shouldAdd];
}

RCT_EXPORT_METHOD(setShouldShowInstructionPage:(BOOL)shouldShow){
   [getDocConfig() setShouldShowInstructionsPage:shouldShow];
   shouldShowInstructionPage = shouldShow;
}

RCT_EXPORT_METHOD(setShouldShowReviewScreen:(BOOL)shouldShow){
  [getDocConfig() setShouldShowReviewPage:shouldShow];
}

RCT_EXPORT_METHOD(start: (RCTResponseSenderBlock)completionHandler) {
  
  HVDocConfig * hvDocConfig = getDocConfig();
  
  UIViewController *root = RCTPresentedViewController();
  
  
  [HVDocsViewController start:root hvDocConfig:hvDocConfig completionHandler:^(HVError* error, HVResponse* _Nonnull result, UIViewController* vcNew){
    
    if(error != nil){
      NSMutableDictionary *errorDict = [[NSMutableDictionary alloc] init];
      NSNumber *errorCode = [NSNumber numberWithInteger:error.getErrorCode];
      NSString *errorMessage = [NSString stringWithString:error.getErrorMessage];
      [errorDict setValue: errorCode forKey: @"errorCode"];
      [errorDict setValue: errorMessage forKey: @"errorMessage"];
      if (result == nil) {
        completionHandler(@[errorDict, [NSNull null]]);
      }else{
        
        NSMutableDictionary *resultDict = [[NSMutableDictionary alloc] init];
        
        if (result.apiResult != nil) {
          NSDictionary<NSString *,id> *apiResult = result.apiResult;
          [resultDict setValue:apiResult forKey: @"apiResult"];
        }
        
        if (result.apiHeaders != nil) {
          NSDictionary<NSString *,id> *apiHeaders = result.apiHeaders;
          [resultDict setValue:apiHeaders forKey: @"apiHeaders"];
        }
        
        if (result.imageUri != nil) {
          NSString *imageUri = [NSString stringWithString:result.imageUri];
          [resultDict setValue:imageUri forKey: @"imageUri"];
        }
        
        if (result.fullImageUri != nil) {
          NSString *fullImageUri = [NSString stringWithString:result.fullImageUri];
          [resultDict setValue:fullImageUri forKey: @"fullImageUri"];
        }
        
        if (result.retakeMessage != nil) {
          NSString *retakeMessage = [NSString stringWithString:result.retakeMessage];
          [resultDict setValue:retakeMessage forKey: @"retakeMessage"];
        }
        
        if (result.action != nil) {
          NSString *action = [NSString stringWithString:result.action];
          [resultDict setValue:action forKey: @"action"];
        }
        
        completionHandler(@[errorDict, resultDict]);
      }
    }else{
      
      NSMutableDictionary *resultDict = [[NSMutableDictionary alloc] init];
      
      if (result.apiResult != nil) {
        
        NSError *error;
        NSData *jsonData = [NSJSONSerialization dataWithJSONObject:result.apiResult options:NSJSONWritingPrettyPrinted error:&error];
        NSString *jsonString = [[NSString alloc] initWithData:jsonData encoding:NSUTF8StringEncoding];

        
        NSDictionary<NSString *,id> *apiResult = result.apiResult;
        
        
        [resultDict setValue:jsonString forKey: @"apiResult"];
      }
      
      if (result.apiHeaders != nil) {
        NSDictionary<NSString *,id> *apiHeaders = result.apiHeaders;
        [resultDict setValue:apiHeaders forKey: @"apiHeaders"];
      }
      
      if (result.imageUri != nil) {
        NSString *imageUri = [NSString stringWithString:result.imageUri];
        [resultDict setValue:imageUri forKey: @"imageUri"];
      }
      
      if (result.fullImageUri != nil) {
        NSString *fullImageUri = [NSString stringWithString:result.fullImageUri];
        [resultDict setValue:fullImageUri forKey: @"fullImageUri"];
      }
      
      if (result.retakeMessage != nil) {
        NSString *retakeMessage = [NSString stringWithString:result.retakeMessage];
        [resultDict setValue:retakeMessage forKey: @"retakeMessage"];
      }
      
      
      if (result.action != nil) {
        NSString *action = [NSString stringWithString:result.action];
        [resultDict setValue:action forKey: @"action"];
      }
      
      completionHandler(@[[NSNull null], resultDict]);
    }
  }];
}

- (dispatch_queue_t)methodQueue
{
  return dispatch_get_main_queue();
}


@end
