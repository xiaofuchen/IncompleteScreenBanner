# IncompleteScreenBanner
一个不显示全屏图的banner无限自动轮播例子，利用recycleview实现类似viewpager效果

##背景
其实程序猿要开发一个demo的背景，都！一！样！
说什么为了社会进步，为了挑战自我，都！是！瞎！扯！蛋！
无非就是一个背景，产品经理要求实现该功能！！！

![生无可恋](https://ss3.bdstatic.com/70cFv8Sh_Q1YnxGkpoWK1HF6hhy/it/u=1217559312,2326813221&fm=27&gp=0.jpg>)

废话小说，先上gif为敬！

![这是一个gif动图](https://github.com/xiaofuchen/IncompleteScreenBanner/blob/master/data/3.gif)

##功能
正常来说，banner无限轮播，都是全屏的图片去轮播，现在产品要求一张图不布满全屏，让下一个图露出一点。。。
（虽然我看不太懂这个设计美感）

## 步骤
###前期准备
找了一下，目测没有现成的轮子，但看了一位大佬写的无限轮播图有感，我就想了一下，卧槽，我可以在这基础上改一波呀。
在这里真诚的感谢一波这位大哥Renny[https://github.com/ren93/RecyclerBanner.git]（https://github.com/ren93/RecyclerBanner.git）
（如有雷同纯属巧合）
###实现原理
* 利用recycleview做一个类似viewpager的效果
* 通过设置adapter的itemCount为Integer.MAX_VALUE，再把默认的第一个item设置到*10000的位置去，曲线实现无限轮播功能
* 继承工具类SnapHelper，实现拖动itemView有viewpager的feel而且自动适配位置回弹

貌似看起来也不难吧

![这是一只发春的狗](https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1515933244001&di=8eabe670c8934ebd576a236d5d7880d7&imgtype=jpg&src=http%3A%2F%2Fimg1.imgtn.bdimg.com%2Fit%2Fu%3D1157079631%2C3553318991%26fm%3D214%26gp%3D0.jpg)

## 重点
其中，继承SnapHelper是重点，这是一个用于辅助RecyclerView在滚动结束时将Item对齐到某个位置，而官方中也有两个子类LinearSnapHelper、PagerSnapHelper，都能让item滑动后自动停留在recycleview的中间。很遗憾，这两个官方的子类不满足我们的功能需求，但是！我可以复制出来改它一波呀！
![哥就是自信](https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1558966099912&di=89ffc67c759b3622c231a377a3538e68&imgtype=0&src=http%3A%2F%2Fimg2.biaoqingjia.com%2Fbiaoqing%2F201806%2Fc1a4c01927482b9543d9a63284fbc8db.gif)
改完了，就依赖关联上recycleview，完美！
`new MyCustomSnapHelper().attachToRecyclerView(mRecyclerView);`

##结束
更多详情，请前往我的github去撸，代码里面都有清楚的注释
[https://github.com/xiaofuchen/IncompleteScreenBanner](https://github.com/xiaofuchen/IncompleteScreenBanner)

顺手给Stars是中华人民的美德
谢谢
![送美女一枚](http://img.mp.itc.cn/upload/20161102/d678f74eb6c440ec9dc9ebb1d7906cc8.gif)

