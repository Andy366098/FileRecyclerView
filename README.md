# FileRecyclerView

多個檔案的讀取寫入的登入畫面練習、RecyclerView呈現效果、checkBox的複選刪除

首先，使用Basic Activity樣板，然後多創一個recycler_item.xml的layout，其他全部貼上。

還有AndroidManifest要給權限

uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"

uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"
 
以及在build.gradle(Module)的dependencies裡要加上implementation 'androidx.swiperefreshlayout:swiperefreshlayout:1.1.0'
這是個下拉刷新的功能

額外還付了一個練習用的跳轉至外部程式的按鈕

注：本人為Android新手，因此有些注釋的用詞可能不太準確，請見諒

展示：
![image](https://github.com/Andy366098/FileRecyclerView/blob/master/Screenshot_20210525-090249816.jpg)
![image](https://github.com/Andy366098/FileRecyclerView/blob/master/Screenshot_20210525-090303022.jpg)

參考網站：https://thumbb13555.pixnet.net/blog/post/311803031
