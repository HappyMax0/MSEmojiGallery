# MSEmojiGallery
 An android app that show microsoft fluent emojis

how to use 
```shell
git clone --recurse-submodules https://github.com/HappyMax0/MSEmojiGallery.git 
```

Update assets
```shell
cd external-assets
git pull origin main
cd ..
git add external-assets
git commit -m "Update Fluent UI Emoji assets"
```

Copy assets to android prject
mac/linux
```shell
ln -s "$(pwd)/external-assets/assets" "$(pwd)/app/src/main/assets"
```

```shell
mklink /D app\src\main\assets external-assets\assets
```