<template>
    <div class="wrapper" @click="onClick">
        <image class="icon" :src="iconImageUrl" :style="iconImageStyle"></image>
        <text class="tips">{{textToShow}}</text>
    </div>
</template>

<script>
import mixin from '@/modules/mixin'

export default {
  mixins: [mixin],
  props: {
    type: {
      type: String,
      default: 'error'
    },
    text: {
      type: String
    },
    retry: {
      type: Function
    }
  },
  computed: {
    iconImageUrl () {
      if (this.type === 'error') return 'http://cn.e.pic.mangatoon.mobi/for-clients/network-error.png'
      if (this.type === 'no-data') return 'http://cn.e.pic.mangatoon.mobi/for-clients/no-data.png'
    },
    iconImageStyle () {
      if (this.type === 'error') return {width: '120px', height: '132px'}
      if (this.type === 'no-data') return {width: '175px', height: '175px'}
    },
    textToShow () {
      if (this.text && this.text !== '' && this.text !== 'undefined') return this.text
      if (this.type === 'error') return ''
      if (this.type === 'no-data') return '这里什么都没有'
    }
  },
  methods: {
    onClick () {
      if (this.retry) this.retry.call(null)
    }
  }
}
</script>

<style scoped>
    .wrapper {
        position: absolute;
        top: 0;
        left: 0;
        right: 0;
        bottom: 0;
        align-items: center;
        justify-content: center;
        /* background-color: #f4f4f4; */
    }

    .icon {
        width: 120px;
        height: 132px;
        margin-bottom: 20px;
    }

    .tips {
        font-size: 28px;
        color: #9f9fac;
        text-align: center;
    }
</style>
