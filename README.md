![封面][img_sd_part]

# SimpleDiffusion：随时随地打开二次元的大门

<div align="center">
<img src="https://img.shields.io/badge/Android-gray?style=for-the-badge&logo=android" alt="Static Badge"/>
<img src="https://img.shields.io/badge/jetpack_compose-gray?style=for-the-badge&logo=jetpackcompose" alt="Static Badge"/>
<br>

<i>一个使用 Stable Diffusion API 的安卓客户端。</i>
<a href="https://github.com/TopSea/SimpleDiffusion/stargazers"><img src="https://img.shields.io/github/stars/TopSea/SimpleDiffusion" alt="Stars Badge"/></a>
<a href="https://github.com/TopSea/SimpleDiffusion/network/members"><img src="https://img.shields.io/github/forks/TopSea/SimpleDiffusion" alt="Forks Badge"/></a>
<a href="https://github.com/TopSea/SimpleDiffusion/pulls"><img src="https://img.shields.io/github/issues-pr/TopSea/SimpleDiffusion" alt="Pull Requests Badge"/></a>
<a href="https://github.com/TopSea/SimpleDiffusion/issues"><img src="https://img.shields.io/github/issues/TopSea/SimpleDiffusion" alt="Issues Badge"/></a>
<a href="https://github.com/TopSea/SimpleDiffusion/graphs/contributors"><img alt="GitHub contributors" src="https://img.shields.io/github/contributors/TopSea/SimpleDiffusion?color=2b9348"></a>
<a href="https://github.com/TopSea/SimpleDiffusion/blob/master/LICENSE"><img src="https://img.shields.io/github/license/TopSea/SimpleDiffusion?color=2b9348" alt="License Badge"/></a>
<br>
<a href="https://github.com/TopSea/SimpleDiffusion/releases"><img src="https://img.shields.io/github/downloads/TopSea/SimpleDiffusion/total" alt="Download Badge"/></a>
<a href="https://play.google.com/store/apps/details?id=top.topsea.simplediffusion"><img src="https://img.shields.io/badge/jetpack_compose-gray?style=for-the-badge&logo=googleplay" alt="Download Badge"/></a>

</div>

[English](./docs/README_EN.md)| 简体中文

## 功能

* 文生图
* 图生图
* 拍摄生图
* 生成队列
* 生成进度的显示
* 中断生成
* Stable Diffusion 脚本支持（X/Y/Z plot、Ultimate SD upscale）
* ControlNet 插件支持
* Agent Scheduler 插件支持
* 快捷提示词插件支持

还有很多定制化的功能，强烈建议下载安装后亲自体验。

## 部分页面截图

| ![截图][screen_1] | ![截图][screen_2] | ![截图][screen_3] | ![截图][screen_4] |
| ----------------- | ----------------- | ----------------- | ----------------- |

## 基础使用攻略

**使用 SimpleDiffusion 前，首先你得有一个能够正常使用的 Stable Diffusion WebUI。**

然后我们来设置 Stable Diffusion 的 ip 地址：

1.打开 Windows 命令行窗口，输入 ipconfig 获得 ip 地址；

![攻略][how_to_use_1]

2.打开绘世设置 server-name 以及启用 api，然后启动（如果使用 bat 运行，请直接看第三步）；

![攻略][how_to_use_2]

3.复制 Stable Diffusion 的链接；

![攻略][how_to_use_3]

4.打开 SimpleDiffusion ，进入到设置；

![攻略][how_to_use_4]

5.修改 Stable Diffusion 服务器的地址，然后返回主页面；

![攻略][how_to_use_5]

![攻略][how_to_use_6]

6.进入参数页面，修改参数；

![攻略][how_to_use_7]

![攻略][how_to_use_8]

7.修改参数的大模型，然后确认退出；

![攻略][how_to_use_9]

8.选用参数并发送生图请求；

![攻略][how_to_use_10]

9.查看生图的进度以及生成的图片；

![攻略][how_to_use_11]
![攻略][how_to_use_12]
![攻略][how_to_use_13]

**SimpleDiffusion 的基础攻略就这样啦。前面的区域，现在就去探索吧！**

[img_sd_part]: ./docs/image/Simple_Diffusion_part.jpg
[screen_1]: ./docs/image/screen1.jpg
[screen_2]: ./docs/image/screen2.jpg
[screen_3]: ./docs/image/screen3.jpg
[screen_4]: ./docs/image/screen4.jpg
[how_to_use_1]: ./docs/image/1.png
[how_to_use_2]: ./docs/image/2.png
[how_to_use_3]: ./docs/image/3.png
[how_to_use_4]: ./docs/image/4.png
[how_to_use_5]: ./docs/image/5.png
[how_to_use_6]: ./docs/image/6.png
[how_to_use_7]: ./docs/image/7.png
[how_to_use_8]: ./docs/image/8.png
[how_to_use_9]: ./docs/image/9.png
[how_to_use_10]: ./docs/image/10.png
[how_to_use_11]: ./docs/image/11.png
[how_to_use_12]: ./docs/image/12.png
[how_to_use_13]: ./docs/image/13.png
