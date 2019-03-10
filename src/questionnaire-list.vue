<template>
    <div>
        <nav-bar show-back="true" title="问卷列表">
            <image slot="right-icon2" class="add-btn" :src="iconPath('icon_upload')" @click="onAdd"></image>
        </nav-bar>
        <list class="list" v-if="lists && lists.length!=0" @loadmore="loadMore">
            <cell v-for="(item,index) in lists" :key="index">
                <div class="cell-div">
                    <text class="title" lines="1">{{item.name}}</text>
                    <div class="btn-div" @click="onEdit(item)">
                        <div :class="['image-btn','margin']">
                            <image class="icon" :src="iconPath('icon_pen')"></image>
                            <text class="blue-text">编辑</text>
                        </div>
                        <div class="image-btn" @click="onDelete(item)">
                            <image class="icon" :src="iconPath('icon_delete')"></image>
                            <text class="blue-text">删除</text>
                        </div>
                    </div>
                </div>
            </cell>
            <cell class="load-cell">
                <more-footer :status="loadingStatus"></more-footer>
            </cell>
        </list>
        <PopupMenu v-if="isPopupShow" @popupClickedWithBtnIndex="popupClickedWithBtnIndex" contentInfo="您确认要删除该问卷吗"
                   iconImage="icon_gth" :statusType="true"></PopupMenu>
    </div>
</template>

<script>
import NavBar from './components/nav-bar.vue'
import api from '@/modules/api'
import mixin from '@/modules/mixin'
import MoreFooter from './components/more-footer'
import user from '@/modules/user'
import PopupMenu from './components/popup-menu'

var navigator = weex.requireModule('navigator')

export default {
  mixins: [mixin],
  components: {NavBar, MoreFooter, PopupMenu},
  data () {
    return {
      lists: [],
      pageNo: 1,
      isInSelectionMode: false,
      isPopupShow: false,
      loadingStatus: 'loading',
      currentItem: null
    }
  },
  created () {
    this.getLists()
    var globalEvent = weex.requireModule('globalEvent');
    globalEvent.addEventListener('questionnaire-changed', function (e) {
      this.lists = []
      this.getLists()
    }.bind(this))
  },
  methods: {
    getLists () {
      let self = this
      api.get('/edu/survey/getSurveyByPage', {pageNo: this.pageNo, pageCount: 10, uid: user.userId()},
        function (data) {
          self.lists = self.lists.concat(data.content.records)
          if (self.lists.length === 0) {
            self.loadingStatus = 'no-data'
          } else {
            if (data.content.total === self.lists.length) {
              self.loadingStatus = 'no-more'
            }
          }
        },
        function (e) {
          weex.requireModule('modal').toast({
            message: e,
            duration: 1000
          })
        })
    },
    loadMore () {
      if (this.loadingStatus === 'no-more' || this.loadingStatus === 'no-data') return
      this.pageNo++
      this.getLists()
    },
    onAdd () {
      navigator.push({
        url: api.getUri('questionnaire'),
        animated: true
      })
    },
    onEdit (item) {
      if (this.isInSelectionMode) {
        weex.requireModule('myevent').sendGlobal('questionnaire-selected', {
          id: item.id,
          name: encodeURIComponent(item.name)
        })
        navigator.pop()
        return
      }
      navigator.push({
        url: api.getUri('questionnaire') + '?type=edit' + '&id=' + item.id,
        animated: true
      })
    },
    onDelete (item) {
      this.currentItem = item
      this.isPopupShow = true
    },
    popupClickedWithBtnIndex (index) {
      let arrayIndex = -1
      for (var i = 0; i < this.lists.length; i++) {
        if (this.lists[i] === this.currentItem) arrayIndex = i
      }
      this.isPopupShow = false
      if (arrayIndex === -1) {
        return
      }

      if (index === 1) {
        let self = this
        api.post('/edu/survey/delSurvey', {id: this.currentItem.id},
          function (data) {
            self.lists.splice(arrayIndex, 1)
          },
          function (e) {
            weex.requireModule('modal').toast({
              message: e,
              duration: 1000
            })
          })
      }
    }

  }
}
</script>

<style scoped>
    .add-btn {
        width: 50px;
        height: 50px;
        position: absolute;
        right: 20px;
        top: 25px;
    }

    .list {
        flex: 1;
        background-color: rgb(240, 240, 240);
    }

    .cell-div {
        width: 710px;
        height: 140px;
        margin-left: 20px;
        background-color: #ffffff;
        border-radius: 10px;
        margin-top: 20px;
    }

    .title {
        font-size: 28px;
        color: #333333;
        margin-left: 20px;
        margin-top: 40px;
    }

    .btn-div {
        flex-direction: row;
        width: 710px;
        padding-right: 20px;
        justify-content: flex-end;
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
