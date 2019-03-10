<template>
    <div class="bg" @click="ignore">
        <div class="content">
            <image class="close" :src="iconPath('icon_close')" @click="onClose"></image>
            <div class="text-container">
                <text class="text">邀请好友一起学习</text>
            </div>
            <div class="btn-container">
                <div class="wx-btn" @click="shareWithWXFriend">
                    <image class="share-btn" :src="iconPath('share_wechat')"></image>
                    <text class="share-btn-text">微信好友</text>
                </div>
                <div class="wx-btn" @click="shareWithWXScence">
                    <image class="share-btn" :src="iconPath('share_pyq')"></image>
                    <text class="share-btn-text">微信朋友圈</text>
                </div>
                <div class="wx-btn" @click="savePic()">
                    <image class="share-btn" :src="iconPath('icon_xz_o')"></image>
                    <text class="share-btn-text">保存图片</text>
                </div>
            </div>
        </div>
    </div>
</template>

<script>
import mixin from '@/modules/mixin'
export default {
    mixins: [mixin],
    props: ['shareParams'],
    methods: {
        ignore() {

        },
        onClose() {
            this.$emit('shareClose')
        },
        shareWithWXFriend() {
            let params = this.shareParams
            params.wxscene= 0
            weex.requireModule('share').shareCourse(params)
            this.onClose()
        },
        shareWithWXScence() {
            let params = this.shareParams
            params.wxscene= 1
            weex.requireModule('share').shareCourse(params)
            this.onClose()
        },
        savePic() {
            weex.requireModule('share').saveCoursePic(this.shareParams)
            this.onClose()
        }
    }
}
</script>

<style scoped>
.bg {
    position: fixed;
    top: 0px;
    right: 0px;
    bottom: 0px;
    left: 0px;
    background-color: rgba(0, 0, 0, 0.4);
}

.content {
    height: 300px;
    position: fixed;
    bottom: 0px;
    left: 0px;
    right: 0px;
    background-color: white;
    flex-direction: column;
    justify-content: center;
    align-items: center;
}

.btn-container {
    width: 750px;
    height: 200px;
    flex-direction: row;
    align-items: center;
}

.wx-btn {
    margin-left: 96px;
    width: 130px;
    height: 160px;
    align-items: center;
}

.text-container {
}

.text {
    height: 100px;
    width: 400px;
    line-height: 100px;
    text-align: center;
    font-size: 32px;
}

.share-btn {
    width: 100px;
    height: 100px;
}

.share-btn-text {
    width: 130px;
    margin-top: 10px;
    font-size: 24px;
    text-align: center;
}

.close {
    width: 40px;
    height: 40px;
    position: fixed;
    bottom: 240px;
    right: 30px;
}
</style>
