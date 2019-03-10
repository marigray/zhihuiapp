<template>
    <div class="popup-container">
        <div class="mask-layer"></div>
        <div class="mask-window">
            <div class="mask-window-title">
                <image class="icon" :src="iconPath('icon_close')" @click="closeWindow"></image>
            </div>
            <div class="popup-content">
                <div class="window-content">
                    <image class="icon-content" :src="iconPath(iconImage)"></image>
                    <text class="content-info">{{contentInfo}}</text>
                </div>
                <div class="btn-container" v-if="statusType">
                    <text class="cancel" @click="btnClicked(0)">取消</text>
                    <div class="gapDiv"></div>
                    <text class="submit" @click="btnClicked(1)">确认</text>
                </div>
                <div class="btn-container" v-else>
                    <text class="submit">确认</text>
                </div>
            </div>
        </div>
    </div>
</template>

<script>
import mixin from '@/modules/mixin'

export default {
  props: ['iconImage', 'contentInfo', 'statusType'],
  mixins: [mixin],
  data() {
    return {
      showWindow: true
    }
  },
  methods: {
    btnClicked (index) {
      this.$emit('popupClickedWithBtnIndex', index)
    }
  }
}
</script>

<style scoped>
    .mask-layer {
        width: 970px;
        height: 1800px;
        background-color: #000;
        align-items: center;
        opacity: 0.5;
        flex-direction: row;
        z-index: 960;
        top: 0;
        left: 0;
        position: fixed;
    }

    .mask-window {
        position: fixed;
        top: 300px;
        left: 20px;
        width: 710px;
        height: 500px;
        border: 1px solid #C6C6C6;
        border-radius: 10px;
        z-index: 970;
        background-color: white;
    }

    .popup-content {
        align-items: center;
    }

    .window-content {
        justify-content: center;
        align-items: center;
        margin: 60px 0;
    }

    .icon-content {
        width: 120px;
        height: 120px;
    }

    .content-info {
        font-size: 30px;
        margin-top: 40px;
    }

    .icon {
        width: 40px;
        height: 40px;
        position: absolute;
        top: 20px;
        right: 20px;
        z-index: 1000;
    }

    .btn-container {
        margin-top: 20px;
        flex-direction: row;
    }

    .cancel {
        width: 280px;
        background-color: rgb(230, 230, 230);
        height: 80px;
        text-align: center;
        line-height: 80px;
        color: rgb(88, 88, 88);
        font-size: 30px;
        border-radius: 10px;
    }

    .gapDiv {
        width: 30px;
    }

    .submit {
        background-color: rgb(6, 58, 149);
        width: 280px;
        height: 80px;
        color: white;
        text-align: center;
        line-height: 80px;
        font-size: 30px;
        border-radius: 10px;
    }
</style>
