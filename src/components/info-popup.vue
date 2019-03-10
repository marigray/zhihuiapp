<template>
    <div>
        <div class="info-container">
            <div class="mask-layer"></div>
            <div class="mask-window">
                <div class="mask-window-title">
                    <image class="icon" :src="iconPath('icon_close')" @click="closeWindow"></image>
                </div>
                <div class="popup-content">
                    <div class="window-content">
                        <text style="font-size:24px;color:#646464">请选择通知时间</text>
                        <text class="button" @click="pickTime" :value="value"></text>
                        <image class="icon" :src="iconPath('icon_time')"></image>
                    </div>
                    <div class="btn-container">
                        <text class="cancel" @click="btnClicked(0)">取消</text>
                        <div class="gapDiv"></div>
                        <text class="submit" @click="btnClicked(1)">确认</text>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<script>
import mixin from '@/modules/mixin'

const picker = weex.requireModule('picker')
export default {
  mixins: [mixin],
  data() {
    return {
      value: ''
    }
  },
  methods: {
    btnClicked(index) {
      this.$emit('infoPopupIndex', index)
    },
    pickTime() {
      picker.pickTime({
        value: this.value
      }, event => {
        if (event.result === 'success') {
          this.value = event.data
        }
      })
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
        margin-top: 120px;
        margin-bottom: 60px;
        width: 670px;
        height: 90px;
        border-width: 1px;
        border-color: rgb(138, 130, 148);
        border-radius: 5px;
        background-color: rgb(255, 255, 255);
        flex-direction: row;
        padding-left: 20px;
        padding-right: 20px;
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

    .button {
        font-size: 24px;
        height: 50px;
        line-height: 50px;
        color: #333333;
        flex: 1;
        margin: 22px;
        margin-left: 50px;
        margin-right: 50px;
        text-align: right;
    }
</style>
