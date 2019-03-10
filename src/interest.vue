<template>
    <div>
        <nav-bar show-back="true" title="学习兴趣"></nav-bar>
        <div class="wrapper">
            <scroller class='scroller'>
                <div class="top">
                    <text style="font-size:38px;color:#333333;">选择你感兴趣的知识</text>
                    <text style="font-size:24px;color:#646464;">至少选择1个可随时调整</text>
                </div>
                <div class="content" v-for="(group, index) in interestList" :key="index">
                    <div class="group-title-div">
                        <div class="head-line"></div>
                        <text style="font-size:32px;color:#333333;margin-left:20px">{{group.name}}</text>
                    </div>
                    <div class='tag-btns-container'>
                        <text v-for="(item, index2) in group.interestChildrenList" :key="index2"
                              :class="['tag-btn',selectIndexArr.indexOf(item.id)>-1?'tag-btn-select':'tag-btn-unselect']"
                              @click="onTagClicked(item.id)">{{item.name}}
                        </text>
                    </div>
                </div>
                <div style="weight:750px;height:100px"></div>
            </scroller>
            <div class="btn-confirm" @click="addInterest">
                <text style="font-size: 32px;color:#ffffff">选好了</text>
            </div>
        </div>
    </div>
</template>

<script>
import NavBar from './components/nav-bar.vue'
import api from '@/modules/api'
import user from '@/modules/user'

var navigator = weex.requireModule('navigator')

export default {
  components: {NavBar},
  data() {
    return {
      interestList: [],
      userInterestList: [],
      selectIndexArr: []
    }
  },
  created() {
    this.getInterestList()
    this.getUserInterestList()
  },
  methods: {
    getInterestList() {
      let self = this
      api.get('/edu/dic/getInterestList', {},
        function (data) {
          self.interestList = data.content
        },
        function (e) {
        })
    },
    getUserInterestList() {
      let self = this
      api.get('/edu/dic/getInterestList', {uid: user.userId()},
        function (data) {
          console.log(data.content)
          var results = data.content
          for (var i = 0; i < results.length; i++) {
            for (var j = 0; j < results[i].interestChildrenList.length; j++) {
              self.selectIndexArr.push(results[i].interestChildrenList[j].id)
            }
          }
        },
        function (e) {
        })
    },
    onTagClicked(index) {
      console.log(index)
      var indexOf = this.selectIndexArr.indexOf(index)
      if (indexOf > -1) {
        this.selectIndexArr.splice(indexOf, 1)
      } else {
        this.selectIndexArr.push(index)
      }
    },
    addInterest() {
      var interestId = this.selectIndexArr.join(',')
      api.post('/edu/dic/addUserInterestList', {uid: user.userId(), iid: interestId},
        function (data) {
          weex.requireModule('modal').toast({
            message: '兴趣添加成功！',
            duration: 1000
          })
          navigator.pop()
        },
        function () {
          weex.requireModule('modal').toast({
            message: '提交失败，请稍后再试',
            duration: 1000
          })
        })
    }
  }
}
</script>

<style scoped>
    .wrapper {
        flex: 1;
        position: relative;
    }

    .scroller {
        position: absolute;
        top: 0;
        width: 750px;
        height: 1120px;
        background-color: white;
    }

    .top {
        width: 750px;
        margin-top: 30px;
        align-items: center;
        justify-content: center;
    }

    .content {
        width: 690px;
        margin-left: 30px;
        margin-right: 30px;
        margin-top: 40px;
    }

    .group-title-div {
        width: 690px;
        flex-direction: row;
        align-items: center;
        justify-content: flex-start;
    }

    .head-line {
        width: 8px;
        height: 35px;
        background-color: rgb(0, 54, 142);
    }

    .tag-btns-container {
        width: 690px;
        display: flex;
        flex-direction: row;
        flex-wrap: wrap;
    }

    .tag-btn {
        height: 60px;
        padding-left: 35px;
        padding-right: 35px;
        border-radius: 45px;
        text-align: center;
        margin-bottom: 20px;
        margin-top: 20px;
        margin-right: 15px;
        font-size: 24px;
        line-height: 60px;
    }

    .tag-btn-select {
        background-color: rgb(15, 54, 145);
        color: #ffffff;
    }

    .tag-btn-unselect {
        border-width: 2px;
        border-style: solid;
        border-color: #dcdcdc;
        color: #333333;
    }

    .btn-confirm {
        position: absolute;
        left: 225px;
        bottom: 20px;
        height: 80px;
        width: 300px;
        justify-content: center;
        align-items: center;
        border-radius: 45px;
        font-size: 32px;
        background-color: rgb(15, 54, 145);
        color: #ffffff;
    }
</style>
