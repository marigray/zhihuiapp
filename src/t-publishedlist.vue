<template>
    <div class="wrapper">
        <nav-bar title="已发布列表" show-back="true"></nav-bar>
        <list class="list">
            <cell class="cell" v-for="(item,index) in publishedlist" :key="index">
                <div class="cell-content">
                    <div class="subrow">
                        <text class="title">课程名称：</text>
                        <text class="content">{{item.name}}</text>
                    </div>
                    <div class="subrow">
                        <text class="title">课程单价：</text>
                        <text class="content">{{item.price}}</text>
                    </div>
                    <div class="subrow">
                        <text class="title">课程类型：</text>
                        <text class="content">{{CourseTypeString(item.type)}}</text>
                    </div>
                    <div class="subrow">
                        <text class="title">上课时间：</text>
                        <text class="content">{{item.startDate}}</text>
                    </div>
                    <div class="subrow">
                        <text class="title">报名人数：</text>
                        <text class="content">{{item.actualNum}}</text>
                    </div>
                    <div class="subrow">
                        <text class="title">课程收入：</text>
                        <text class="content">{{item.income}}</text>
                    </div>
                </div>
            </cell>
        </list>
    </div>
</template>

<script>
import NavBar from './components/nav-bar.vue'

var navigator = weex.requireModule('navigator')

export default {
  components: {NavBar},
  data () {
    return {
      publishedlist:
        [
          {
            title: '伙伴教练心关系The Heart Of Coaching',
            price: '￥200元',
            type: '视频课程',
            time: '2018-06-01 16:00-17:00',
            people: '200人',
            income: '24000元'
          },
          {
            title: '伙伴教练心关系The Heart Of Coaching',
            price: '￥200元',
            type: '视频课程',
            time: '2018-06-01 16:00-17:00',
            people: '200人',
            income: '24000元'
          },
          {
            title: '伙伴教练心关系The Heart Of Coaching',
            price: '￥200元',
            type: '视频课程',
            time: '2018-06-01 16:00-17:00',
            people: '200人',
            income: '24000元'
          }
        ]
    }
  },
  methods: {
    onNavigator (link) {
      navigator.push({
        url: 'zhihui://' + link,
        animated: true
      })
    }
  }
}
</script>

<style scoped>
    .wrapper {
        background-color: rgb(240, 240, 240);
    }

    .list {
        flex: 1;
    }

    .cell-content {
        height: 320px;
        margin: 20px;
        box-shadow: 0px 0px 20px 0px rgba(26, 53, 140, 0.15);
        align-items: flex-start;
        justify-content: space-around;
        background-color: white;
        padding-top: 20px;
        padding-bottom: 20px;
    }

    .subrow {
        width: 650px;
        margin-left: 30px;
        margin-right: 30px;
        flex-direction: row;
        align-items: center;
        justify-content: flex-start;
    }

    .title {
        font-size: 24px;
        color: #333333;
    }

    .content {
        font-size: 24px;
        color: #0f3691;
    }
</style>
