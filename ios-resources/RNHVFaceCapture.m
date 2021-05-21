//
//  RNHVFaceCapture.m
//  HyperSnapSDK
//
//  Copyright © 2018 HyperVerge. All rights reserved.

#import "RNHVFaceCapture.h"
#import "AppDelegate.h"

@implementation RNHVFaceCapture

RCT_EXPORT_MODULE()

HVFaceConfig * hvFaceConfig;

HVFaceConfig* getFaceConfig(){
  if (hvFaceConfig == NULL){
    hvFaceConfig = [[HVFaceConfig alloc] init];
  }
  return hvFaceConfig;
}

RCT_EXPORT_METHOD(setLivenessMode:(LivenessMode)mode){
  [getFaceConfig() setLivenessMode:mode];
}

RCT_EXPORT_METHOD(setShouldShowInstructionPage:(BOOL)shouldShow){
  [getFaceConfig() setShouldShowInstructionsPage:shouldShow];
}

RCT_EXPORT_METHOD(setShouldReturnFullImageUri:(BOOL)shouldReturn){
 [getFaceConfig() setShouldReturnFullImageUri:shouldReturn];
}

RCT_EXPORT_METHOD(setLivenessAPIParameters:(NSDictionary<NSString *,id> * _Nullable)parameters){
  [getFaceConfig() setLivenessAPIParameters:parameters];
}

RCT_EXPORT_METHOD(setLivenessAPIHeaders:(NSDictionary<NSString *,NSString *> * _Nullable)headers){
  [getFaceConfig() setLivenessAPIHeaders:headers];
}

RCT_EXPORT_METHOD(setLivenessEndpoint:(NSString *)endpoint){
  [getFaceConfig() setLivenessEndpoint:endpoint];
}

RCT_EXPORT_METHOD(setShouldUseBackCamera:(BOOL)shouldUse){
  [getFaceConfig() setShouldUseBackCamera:shouldUse];
}

RCT_EXPORT_METHOD(setShouldShowCameraSwitchButton:(BOOL)shouldShow){
  [getFaceConfig() setShouldShowCameraSwitchButton:shouldShow];
}

RCT_EXPORT_METHOD(setFaceCaptureTitle:(NSString *)title) {
  [getFaceConfig().textConfig setFaceCaptureTitle:title];
}

// Assumes input like "#00FF00" (#RRGGBB).
- (UIColor *)colorFromHexString:(NSString *)hexString {
  unsigned rgbValue = 0;
  NSScanner *scanner = [NSScanner scannerWithString:hexString];
  [scanner setScanLocation:1]; // bypass '#' character
  [scanner scanHexInt:&rgbValue];
  return [UIColor colorWithRed:((rgbValue & 0xFF0000) >> 16)/255.0 green:((rgbValue & 0xFF00) >> 8)/255.0 blue:(rgbValue & 0xFF)/255.0 alpha:1.0];
}

RCT_EXPORT_METHOD(setUIStrings:(NSString *) uiStrings) {
  NSData *data = [uiStrings dataUsingEncoding:NSUTF8StringEncoding];
  NSDictionary *json = [NSJSONSerialization JSONObjectWithData:data options:0 error:nil];
  
  if ([json objectForKey:@"faceCaptureTitle"] != nil) {
    [getFaceConfig().textConfig setFaceCaptureTitle:[json objectForKey:@"faceCaptureTitle"]];
  }
  
  if ([json objectForKey:@"faceCaptureMoveCloser"] != nil) {
    [getFaceConfig().textConfig setFaceCaptureMoveAwayText:[json objectForKey:@"faceCaptureMoveCloser"]];
  }
  
  if ([json objectForKey:@"faceCaptureFaceFound"] != nil) {
    [getFaceConfig().textConfig setFaceCaptureFaceFoundText:[json objectForKey:@"faceCaptureFaceFound"]];
  }
  
  if ([json objectForKey:@"faceCaptureFaceNotFound"] != nil) {
    [getFaceConfig().textConfig setFaceCaptureFaceNotFoundText:[json objectForKey:@"faceCaptureFaceNotFound"]];
  }
  
  if ([json objectForKey:@"faceCaptureMoveAway"] != nil) {
    [getFaceConfig().textConfig setFaceCaptureMoveAwayText2:[json objectForKey:@"faceCaptureMoveAway"]];
  }
  
  if ([json objectForKey:@"faceCaptureMultipleFaces"] != nil) {
    [getFaceConfig().textConfig setFaceCaptureMultipleFacesDetectedText:[json objectForKey:@"faceCaptureMultipleFaces"]];
  }
  
  if ([json objectForKey:@"faceRetakeTitleText"] != nil) {
    [getFaceConfig().textConfig setFaceRetakeTitleText:[json objectForKey:@"faceRetakeTitleText"]];
  }
  
  if ([json objectForKey:@"faceRetakeButtonText"] != nil) {
    [getFaceConfig().textConfig setFaceRetakeTitleText:[json objectForKey:@"faceRetakeButtonText"]];
  }

}

RCT_EXPORT_METHOD(setFaceCaptureCircleSuccessColor:(NSString *)color){
  UIColor * uiColor = [self colorFromHexString:color];
  [getFaceConfig() setFaceCaptureCircleSuccessColor:uiColor];
}

RCT_EXPORT_METHOD(setFaceCaptureCircleFailureColor:(NSString *)color){
  UIColor * uiColor = [self colorFromHexString:color];
  [getFaceConfig() setFaceCaptureCircleFailureColor:uiColor];
}

RCT_EXPORT_METHOD(setShouldEnablePadding:(BOOL)shouldEnable){
  [getFaceConfig() setShouldEnablePadding:shouldEnable];
}

RCT_EXPORT_METHOD(setPadding:(float)left right:(float)right top:(float)top bottom:(float)bottom){
  [getFaceConfig() setPaddingWithLeft:left right:right top:top bottom:bottom];
}





RCT_EXPORT_METHOD(start: (RCTResponseSenderBlock)completionHandler) {
  
  HVFaceConfig * hvFaceConfig = getFaceConfig();
  
  UIViewController *root = RCTPresentedViewController();
  
  
  [HVFaceViewController start:root hvFaceConfig:hvFaceConfig completionHandler: ^( HVError* _Nullable error, HVResponse* _Nullable result, UIViewController* vcNew){
    
    if(error != nil){
      NSMutableDictionary *errorDict = [[NSMutableDictionary alloc] init];
      NSNumber *errorCode = [NSNumber numberWithInteger:error.getErrorCode];
      NSString *errorMessage = [NSString stringWithString:error.getErrorMessage];
      [errorDict setValue: errorCode forKey: @"errorCode"];
      [errorDict setValue: errorMessage forKey: @"errorMessage"];
      if (result == nil) {
        completionHandler(@[errorDict, [NSNull null]]);
      }else {

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
        NSDictionary<NSString *,id> *apiResult = result.apiResult;
        [resultDict setValue:apiResult forKey: @"apiResult"];
      }
      
      if (result.apiHeaders != nil) {
        NSDictionary<NSString *,id> *apiHeaders = result.apiHeaders;
        [resultDict setValue:apiHeaders forKey: @"apiHeaders"];
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
