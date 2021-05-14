# Android 网络请求库

  [![](https://jitpack.io/v/quickmob/QuickHttp.svg)](https://jitpack.io/#quickmob/QuickHttp)

#### 集成步骤

* 在项目根目录下的 `build.gradle` 文件中加入

```
buildscript {
    ......
}
allprojects {
    repositories {
        maven { url 'https://jitpack.io' }
    }
}
```

* 在项目 app 模块下的 `build.gradle` 文件中加入

```
android {
    compileOptions {
        targetCompatibility JavaVersion.VERSION_1_8
        sourceCompatibility JavaVersion.VERSION_1_8
    }
}

dependencies {
    //框架依赖：其中latest.release指代最新版本号，也可以指定明确的版本号，例如1.0.1
    implementation 'com.github.quickmob:QuickHttp:latest.release'
    //okhttp3依赖
    implementation 'com.squareup.okhttp3:okhttp:3.12.13'
}
```

#### 使用方式
  具体看demo

#### 混淆配置
  此框架库自带混淆规则，并且会自动导入，正常情况下无需手动导入
