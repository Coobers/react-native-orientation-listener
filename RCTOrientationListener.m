//
//  RCTOrientationListener.m
//
//  Created by Ken Wheeler on 9/9/15.
//  Copyright (c) 2015 Facebook. All rights reserved.
//

#import "RCTOrientationListener.h"
#import "RCTBridge.h"

@implementation RCTOrientationListener

@synthesize bridge = _bridge;

- (instancetype)init
{
    if ((self = [super init])) {
        [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(deviceOrientationDidChange:) name:@"UIDeviceOrientationDidChangeNotification" object:nil];
    }
    return self;
    
}

- (void)dealloc
{
    [[NSNotificationCenter defaultCenter] removeObserver:self];
}

- (void)deviceOrientationDidChange:(NSNotification *)notification
{
    
    UIApplication *sharedApplication = [UIApplication sharedApplication];
    UIInterfaceOrientation orientation = [sharedApplication statusBarOrientation];
    
    NSString *orientationStr;
    switch (orientation) {
        case UIInterfaceOrientationPortrait:
        case UIInterfaceOrientationPortraitUpsideDown:
            orientationStr = @"PORTRAIT";
            break;
        case UIInterfaceOrientationLandscapeLeft:
        case UIInterfaceOrientationLandscapeRight:
            
            orientationStr = @"LANDSCAPE";
            break;
        default:
            orientationStr = @"PORTRAIT";
            break;
    }
    
    UIDevice *currentDevice = [UIDevice currentDevice];
    
    NSString *deviceStr = [currentDevice model];
    
    [_bridge.eventDispatcher sendDeviceEventWithName:@"orientationDidChange"
                                                body:@{@"orientation": orientationStr,@"device": deviceStr}];
}

RCT_EXPORT_MODULE();

RCT_EXPORT_METHOD(getOrientation:(RCTResponseSenderBlock)callback)
{
    
    UIApplication *sharedApplication = [UIApplication sharedApplication];
    UIInterfaceOrientation orientation = [sharedApplication statusBarOrientation];
    
    NSString *orientationStr;
    switch (orientation) {
        case UIInterfaceOrientationPortrait:
        case UIInterfaceOrientationPortraitUpsideDown:
            orientationStr = @"PORTRAIT";
            break;
        case UIInterfaceOrientationLandscapeLeft:
        case UIInterfaceOrientationLandscapeRight:
            
            orientationStr = @"LANDSCAPE";
            break;
        default:
            orientationStr = @"PORTRAIT";
            break;
    }
    
    UIDevice *currentDevice = [UIDevice currentDevice];
    NSString *deviceStr = [currentDevice model];
    
    NSArray *orientationArray = @[orientationStr, deviceStr];
    callback(orientationArray);
}

@end
