//
//  MeCardParser.m
//  ZXing
//
//  Created by Christian Brunschen on 25/06/2008.
/*
 * Copyright 2008 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

#import "MeCardParser.h"
#import "BusinessCardParsedResult.h"

@implementation MeCardParser

+ (void)load {
  [ResultParser registerResultParserClass:self];
}

+ (ParsedResult *)parsedResultForString:(NSString *)s {
  NSRange foundRange = [s rangeOfString:@"MECARD:"];
  if (foundRange.location == NSNotFound) {
    return nil;
  }
  
  NSString *name = [s fieldWithPrefix:@"N:"];
  if (name == nil) {
    return nil;
  }
  
  BusinessCardParsedResult *result = [[BusinessCardParsedResult alloc] init];
  result.name = name;
  result.phoneNumbers = [s fieldsWithPrefix:@"TEL:"];
  result.email = [s fieldWithPrefix:@"EMAIL:"];
  result.note = [s fieldWithPrefix:@"NOTE:"];
  result.urlString = [s fieldWithPrefix:@"URL:"];
  result.address = [s fieldWithPrefix:@"ADR:"];
  
  //The following tags are not stricty parot of MECARD spec, but as their are standard in
  //vcard, we honor them
  result.organization = [s fieldWithPrefix:@"ORG:"];
  result.jobTitle = [s fieldWithPrefix:@"TITLE:"];
  
  return [result autorelease];
}


@end
