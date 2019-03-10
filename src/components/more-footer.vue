<template>
    <div class="footer" :style="{height:footerHeight+'px'}">
        <myloading class="loading" v-if="status == 'loading' || status == 'normal'"></myloading>
        <image class="page-error-tips" v-if="status == 'no-data' || status == 'error'" :retry="onRetry" :src="iconPath('a02')"></image>
        <text class="text">{{text}}</text>
    </div>
</template>

<style scoped>
    .footer {
        width: 750px;
        height: 100px;
        justify-content: center;
        align-items: center;
        flex-direction: row;
    }

    .loading {
        width: 60px;
        height: 60px;
    }

    .page-error-tips {
        width: 100px;
        height: 100px;
    }

    .text {
        margin-left: 20px;
        font-size: 30px;
    }
</style>

<script>
import mixin from '@/modules/mixin.js'

export default {
  mixins: [mixin],
  components: {},
  props: ['status'], // normal, loading, no-more, error
  computed: {
    text () {
      if (this.status === 'error') {
        return '加载失败，点击重试'
      }
      if (this.status === 'no-more') {
        return ''
      }
      if (this.status === 'no-data') {
        return '暂无相关内容'
      }
      return '加载中...'
    },
    footerHeight () {
      if (this.status === 'error' || this.status === 'no-data') {
        return 160
      }
      if (this.status === 'no-more') {
        return 0
      }
      return 80
    }
  },
  methods: {
    onRetry () {
      this.$emit('moreFooterRetry')
    }
  }

}
</script>
