# MSEmojiGallery
 An android app that show microsoft fluent emojis

How to use?
First, clone
```shell
git clone --recurse-submodules https://github.com/HappyMax0/MSEmojiGallery.git 
```

After Clone
Update assets from [Fluent Emoji](https://github.com/microsoft/fluentui-emoji)
```shell
cd external-asset
git pull origin main
cd ..
git add external-asset
git commit -m "Update Fluent UI Emoji assets"
```

Then
Copy assets to android prject's assets folder

Mac/Linux
```shell
cp -r external-asset/assets app/src/main/assets
```
Windows
```shell
xcopy external-asset\assets app\src\main\assets /E /I
```