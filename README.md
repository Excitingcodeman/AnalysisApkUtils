# AnalysisApkUtils
获取android apk文件的 [0]:版本号;[1]包名 [2]是版本code；在app版本更新过程中校验apk文件的有效性


使用
  导入jar包后，在需要校验的地方直接调用AnalysisApkUtils类的analysisApk方法即可。analysisApk支持参数是文件路径和文件两种形式。
  对版本号的获取校验最好在子线程中进行。
