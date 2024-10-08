// Copyright (c) 2021 WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
//
// WSO2 Inc. licenses this file to you under the Apache License,
// Version 2.0 (the "License"); you may not use this file except
// in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// KIND, either express or implied.  See the License for the
// specific language governing permissions and limitations
// under the License.

// configurable var is implicitly final and readonly
type UserInfo record {|
    readonly string username;
    string password;
|};

configurable UserInfo admin = {
    username: "default",
    password: "password"
};

public type CustomConfiguration record {
    string username;
    string logLevel = "OFF";
};

configurable CustomConfiguration customConfig1 = {username: "chiranS"};

configurable CustomConfiguration customConfig2 = {username: "chiranS", logLevel: "DEBUG"};

function testConfigValue() {
    assertEquality("default", admin.username);
    assertEquality("password", admin.password);
    assertEquality("chiranS", customConfig1.username);
    assertEquality("OFF", customConfig1.logLevel);
    assertEquality("chiranS", customConfig2.username);
    assertEquality("DEBUG", customConfig2.logLevel);
}

function assertEquality(any|error expected, any|error actual) {
    if expected is anydata && actual is anydata && expected == actual {
        return;
    }

    if expected === actual {
        return;
    }

    string expectedValAsString = expected is error ? expected.toString() : expected.toString();
    string actualValAsString = actual is error ? actual.toString() : actual.toString();
    panic error("AssertionError", message = "expected '" + expectedValAsString + "', found '" + actualValAsString + "'");
}
