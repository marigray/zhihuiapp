<template>
    <div>
        <nav-bar title="课前调查问卷" show-back="true"></nav-bar>
        <list class="list" show-scrollbar=false>
            <cell v-for="(item,index) in courseList" :key="index">
                <div class="row">
                    <text class="title" lines="1">{{item.title}}</text>
                    <div class="date-div">
                        <text class="date-text">发布日期：{{item.time}}</text>
                        <div class="btn-div">
                            <div :class="['image-btn','margin']">
                                <image class="icon" :src="iconPath('icon_pen')"></image>
                                <text class="blue-text">编辑</text>
                            </div>
                            <div class="image-btn">
                                <image class="icon" :src="iconPath('icon_delete')"></image>
                                <text class="blue-text">删除</text>
                            </div>
                        </div>
                    </div>
                </div>
            </cell>
        </list>
    </div>
</template>

<script>
import NavBar from './components/nav-bar.vue'
import mixin from '@/modules/mixin'

var navigator = weex.requireModule('navigator')

export default {
  mixins: [mixin],
  components: {NavBar},
  data () {
    return {
      courseList: [
        {
          title: '伙伴教练心关系调查问卷',
          time: '2018-06-02'
        },
        {
          title: '伙伴教练心关系问卷',
          time: '2018-06-01'
        }
      ]
    }
  },
  methods: {
    onNavigatorPop () {
      navigator.pop()
    }
  }
}
</script>

<style scoped>
    .list {
        flex: 1;
        background-color: rgb(249, 249, 249);
    }

    .row {
        width: 710px;
        height: 140px;
        margin-left: 20px;
        background: #ffffff;
        border-radius: 10px;
        margin-top: 20px;
    }

    .title {
        font-size: 28px;
        color: #333333;
        margin-left: 20px;
        margin-top: 20px;
        margin-bottom: 20px;
    }

    .date-div {
        margin-left: 20px;
        margin-right: 20px;
        flex-direction: row;
        justify-content: space-between;
        align-items: center;
    }

    .date-text {
        font-size: 24px;
        color: #646464;
    }

    .btn-div {
        flex-direction: row;
        justify-content: right;
        align-items: center;
    }

    .image-btn {
        width: 100px;
        height: 50px;
        border-radius: 5px;
        border-style: solid;
        background-color: rgb(255, 255, 255);
        flex-direction: row;
        justify-content: center;
        align-items: center;
    }

    .margin {
        margin-right: 50px;
    }

    .icon {
        width: 30px;
        height: 30px;
    }

    .blue-text {
        font-size: 24px;
        color: #0f3691;
    }
</style>
