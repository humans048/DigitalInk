# 驗證紀錄

- 測試環境：Windows / JDK 21 / Android SDK 36 / School_Hello_API30 模擬器。
- assembleDebug 成功產生 APK。
- testDebugUnitTest：5 項測試，0 失敗、0 錯誤。
- lintDebug 通過。仍有較新依附版本、target SDK 和備份設定建議，沒有錯誤。
- 模擬器 8 項操作檢查全數通過，細節見 verification.json。
- 實際用三個獨立筆畫寫「三」，模型下載完成並回傳「辨識結果: 三」。
- 驗證空白辨識提示、清除後資料清空、開始辨識後立即清除不會再冒出舊結果。
- 橫向模式保留筆跡與辨識結果，兩顆按鈕及結果仍可見，完成後回復原方向。
- 圖片是實際 Android 模擬器畫面，不是設計稿。
- 未提交到學校平台。

目前這部電腦的 Android Studio 內建 Java 是 25，這個 Gradle 版本請使用既有的 JDK 21。
專案本機設定已指定 school/.android-toolchain/jdk-21。若將 ZIP 搬到另一台電腦，請在 Gradle JDK 選擇該機的 JDK 21。
