<template>
    <div>
        <navbar show-back="true" title="消息中心"></navbar>
        <div class='tab-view'>
            <div class='tab-btn' v-for="(item,index) in tablist" :key="index" @click="onTabClicked(index)">
                <text :class="['tab-text' ,tabIndex==index?'tab-text-selected':'']">{{item}}</text>
                <div :class="['tab-line', tabIndex==index?'tab-line-selected':'']"></div>
            </div>
        </div>
        <slider class="slider" :infinite="false" :index="tabIndex" @change="change">
            <message-list type="1"></message-list>
            <message-list type="2"></message-list>
            <private-message></private-message>
        </slider>
    </div>
</template>

<script>
import navbar from './components/nav-bar.vue'
import mixin from '@/modules/mixin'
import PageLoadError from './components/page-load-error'
import MessageList from './message-list'
import PrivateMessage from './private-message'

export default {
  mixins: [mixin],
  components: {MessageList, navbar, PageLoadError, PrivateMessage},
  data () {
    return {
      tabIndex: 0,
      tablist: ['平台消息', '课程消息', '私信']
    }
  },
  created () {
  },
  methods: {
    onTabClicked (index) {
      this.tabIndex = index
    },
    change (e) {
      this.tabIndex = e.index
    }
  }
}
</script>

<style scoped>
    .slider {
        flex: 1;
    }

    .tab-view {
        width: 750px;
        height: 80px;
        background-color: rgb(245, 245, 245);
        display: flex;
        flex-direction: row;
    }

    .tab-btn {
        display: flex;
        flex: 1;
        align-items: center;
        justify-content: center;
        flex-direction: column;
    }

    .tab-text {
        margin-top: 20px;
        font-size: 28px;
    }

    .tab-text-selected {
        color: #0f3691;
    }

    .tab-line {
        margin-top: 20px;
        width: 120px;
        height: 5px;
        background-color: #003797;
        opacity: 0;
    }

    .tab-line-selected {
        opacity: 1;
    }

</style>
