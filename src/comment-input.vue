<template>
    <div class="bg" :style="{height:height+'px'}">
        <div class="input-bg" @click="onInputBarClick">
            <text class="placeholder" v-if="text.length === 0">{{placeholder}}</text>
            <textarea :autofocus="autofocus" ref="input" class="input" @input="onInput" :value="text" @blur="blur"
                      :rows="5" @keyboard="onKeyboard"></textarea>
        </div>
        <div class="btn-send-container" @click="onClick">
            <image class="btn-send" :src="sendBtnImage"></image>
        </div>
    </div>
</template>

<script>
import mixin from '@/modules/mixin.js'

export default {
  mixins: [mixin],
  props: ['placeholder', 'autofocus'],
  data () {
    return {
      text: '',
      height: 110
    }
  },
  computed: {
    sendBtnImage () {
      if (this.text && this.text.length) {
        return 'http://cn.e.pic.mangatoon.mobi/for-clients/comment-icon-send.png'
      }
      return 'http://cn.e.pic.mangatoon.mobi/for-clients/comment-icon-send-disable.png'
    }
  },
  methods: {
    clearInput () {
      // 安卓端直接设置清空没有效果
      this.text = ' '
      this.$nextTick(function () {
        this.text = ''
      }.bind(this))
      this.$refs['input'].blur()
    },
    onInput (e) {
      this.text = e.value
    },
    onInputBarClick (e) {
      this.$refs['input'].focus()
    },
    onClick () {
      if (this.text.length === 0) return
      this.$emit('send', this.text)
    },
    onKeyboard (e) {
      if (e.isShow) {
        this.height = 200
      } else {
        this.height = 110
      }
      this.$emit('keyboard', e)
    },
    blur () {
      this.$emit('blur')
    }
  }
}
</script>

<style scoped>
    .bg {
        border-top-width: 1px;
        border-color: #e3e3e3;
        flex-direction: row;
        width: 750px;
        background-color: white;
    }

    .input-bg {
        flex: 1;
        border-radius: 10px;
        margin-left: 20px;
        width: 590px;
        margin-top: 23px;
        margin-bottom: 23px;
        background-color: rgb(244, 244, 244);
    }

    .input {
        flex: 1;
        font-size: 28px;
        background: transparent;
    }

    .btn-send {
        width: 100px;
        height: 100px;
    }

    .btn-send-container {
        align-items: center;
        justify-content: center;
    }

    .placeholder {
        top: 0;
        bottom: 0;
        left: 10px;
        right: 0;
        font-size: 28px;
        line-height: 64px;
        position: absolute;
        color: #c0c0c0;
    }
</style>
