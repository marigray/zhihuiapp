<template>
    <div>
        <navbar show-back="true" title="课程评价"></navbar>
        <div class='tab-view'>
            <div class='tab-btn' v-for="(item,index) in tablist" :key="index" @click="onTabClicked(index)">
                <text :class="['tab-text' ,tabIndex==index?'tab-text-selected':'']">{{item}}</text>
                <div :class="['tab-line', tabIndex==index?'tab-line-selected':'']"></div>
            </div>
        </div>
        <div class="line"></div>

        <list class='system' v-if="tabIndex==0">
            <cell v-for="(item,index) in latestList" class="system-cell" :key="index">
                <div class="system-cell-content">
                    <image :src='item.avatar' class='avatar'></image>
                    <div class="system-cell-right">
                        <div class="name-line">
                            <text class="name-text">{{item.name}}</text>
                            <text class="date-text">{{item.date}}</text>
                        </div>
                        <text class="content-text">{{item.content}}</text>
                        <div class="chat-line">
                            <image class="chat-image" :src="iconPath('icon_chat')"></image>
                        </div>
                    </div>
                </div>
                <div class="line2"></div>
            </cell>
        </list>

        <list class='system' v-if="tabIndex==1">
            <cell v-for="(item,index) in repliedList" class="system-cell" :key="index">
                <div class="system-cell-content">
                    <image :src='item.avatar' class='avatar'></image>
                    <div class="system-cell-right">
                        <div class="name-line">
                            <text class="name-text">{{item.name}}</text>
                            <text class="date-text">{{item.date}}</text>
                        </div>
                        <text class="content-text">{{item.content}}</text>
                        <div class="reply-line">
                            <text class="me-text">我：</text>
                            <text lines='2' class="reply-text">{{item.reply}}</text>
                        </div>
                    </div>
                </div>
                <div class="line2"></div>
            </cell>
        </list>

    </div>
</template>

<script>
import navbar from './components/nav-bar.vue'
import mixin from '@/modules/mixin'

export default {
  mixins: [mixin],
  components: {navbar},
  data () {
    return {
      tabIndex: 1,
      tablist: ['最新', '已回复'],
      latestList: [
        {
          avatar: 'https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1535078697&di=7af264c5c37508dbf94ab079d257fce8&imgtype=jpg&er=1&src=http%3A%2F%2Fimg.zcool.cn%2Fcommunity%2F01abac55b1b0bd6ac725ca501c700e.jpg',
          name: '西西果',
          date: '2018-06-01',
          content: '老师讲课风趣幽默，将知识点比喻的很形象生动，讲的内容通俗易懂，非常全面的入门课程。'
        },
        {
          avatar: 'https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1535078697&di=7af264c5c37508dbf94ab079d257fce8&imgtype=jpg&er=1&src=http%3A%2F%2Fimg.zcool.cn%2Fcommunity%2F01abac55b1b0bd6ac725ca501c700e.jpg',
          name: 'ytk152008',
          date: '2018-06-01',
          content: '老师讲课风趣幽默，将知识点比喻的很形象生动，讲的内容通俗易懂，非常全面的入门课程。'
        },
        {
          avatar: 'https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1535078697&di=7af264c5c37508dbf94ab079d257fce8&imgtype=jpg&er=1&src=http%3A%2F%2Fimg.zcool.cn%2Fcommunity%2F01abac55b1b0bd6ac725ca501c700e.jpg',
          name: '晨西',
          date: '2018-06-01',
          content: '老师讲课风趣幽默，将知识点比喻的很形象生动，讲的内容通俗易懂，非常全面的入门课程。'
        }
      ],
      repliedList: [
        {
          avatar: 'https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1535078697&di=7af264c5c37508dbf94ab079d257fce8&imgtype=jpg&er=1&src=http%3A%2F%2Fimg.zcool.cn%2Fcommunity%2F01abac55b1b0bd6ac725ca501c700e.jpg',
          name: '西西果',
          date: '2018-06-01',
          content: '老师讲课风趣幽默，将知识点比喻的很形象生动，讲的内容通俗易懂，非常全面的入门课程。',
          reply: '回复内容回复内容回复内容'
        },
        {
          avatar: 'https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1535078697&di=7af264c5c37508dbf94ab079d257fce8&imgtype=jpg&er=1&src=http%3A%2F%2Fimg.zcool.cn%2Fcommunity%2F01abac55b1b0bd6ac725ca501c700e.jpg',
          name: 'ytk152008',
          date: '2018-06-01',
          content: '老师讲课风趣幽默，将知识点比喻的很形象生动，讲的内容通俗易懂，非常全面的入门课程。',
          reply: '回复内容回复内容回复内容回复内容回复内容回复内容'
        },
        {
          avatar: 'https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1535078697&di=7af264c5c37508dbf94ab079d257fce8&imgtype=jpg&er=1&src=http%3A%2F%2Fimg.zcool.cn%2Fcommunity%2F01abac55b1b0bd6ac725ca501c700e.jpg',
          name: '晨西',
          date: '2018-06-01',
          content: '老师讲课风趣幽默，将知识点比喻的很形象生动，讲的内容通俗易懂，非常全面的入门课程。',
          reply: '回复内容回复内容回复内容回复内容回复内容回复内容回复内容回复内容回复内容'
        }
      ]
    }
  },
  methods: {
    onTabClicked (index) {
      this.tabIndex = index
    }
  }
}
</script>

<style scoped>
    .tab-view {
        width: 750px;
        height: 80px;
        background-color: rgb(245, 245, 245);
        display: flex;
        flex-direction: row;
    }

    .line {
        width: 750px;
        height: 2px;
        background-color: #c8c8c8;
    }

    .line2 {
        width: 690px;
        height: 2px;
        margin-left: 30px;
        background-color: #c8c8c8;
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
        width: 100px;
        height: 5px;
        background-color: #003797;
        opacity: 0;
    }

    .tab-line-selected {
        opacity: 1;
    }

    .system {
        flex: 1;
    }

    .system-cell {
        display: flex;
        flex-direction: column;
    }

    .system-cell-content {
        display: flex;
        flex-direction: row;
        justify-content: flex-start;
    }

    .avatar {
        margin-left: 30px;
        margin-top: 40px;
        width: 80px;
        height: 80px;
        border-radius: 40px;
    }

    .system-cell-right {
        width: 590px;
        margin-left: 20px;
    }

    .name-line {
        flex-direction: row;
        justify-content: space-between;
        margin-top: 60px;
    }

    .name-text {
        font-size: 28px;
        color: #646464;
    }

    .date-text {
        font-size: 20px;
        color: #969696;
    }

    .content-text {
        font-size: 24px;
        color: #333333;
        margin-top: 40px;
    }

    .chat-line {
        flex-direction: row;
        justify-content: flex-end;
        width: 590px;
        margin-top: 20px;
        margin-bottom: 20px;
    }

    .chat-image {
        height: 30px;
        width: 30px;
    }

    .reply-line {
        flex-direction: row;
        height: 110px;
        width: 590px;
        margin-top: 20px;
        margin-bottom: 20px;
        background-color: #f0f0f0;
        border-radius: 5px;
        padding-top: 20px;
        padding-left: 15px;
        padding-right: 30px;
    }

    .me-text {
        font-size: 24px;
        color: #0f3691;
    }

    .reply-text {
        font-size: 24px;
        color: #333333;
        word-break: break-all;
        word-wrap: break-word;
        max-width: 520px;
    }
</style>
