<template>
    <list class='list' :style="{height:deviceHeight+'px'}">
        <cell v-for="(item,index) in messageList" class='personal-cell' :key="index" @click="onDetail(item)">
            <div class="personal-cell-content">
                <image :src="(item.sendFromId != currentUserId) ? item.sendFromHeadimag : item.sendToHeadimag" class='header'></image>
                <div class='personal-cell-right-content'>
                    <div class="cell-name-row">
                        <text class='personal-nick-name'>{{(item.sendFromId != currentUserId) ? item.sendFromName : item.sendToName}}</text>
                        <text class='system-date'>{{formatDate(item.sendDate/1000)}}</text>
                    </div>
                    <text class='personal-content'>{{item.content}}</text>
                </div>
            </div>
        </cell>
        <cell class="load-cell">
            <more-footer :status="loadingStatus"></more-footer>
        </cell>
    </list>
</template>

<script>
import api from '@/modules/api'
import user from '@/modules/user'
import PageLoadError from './components/page-load-error'
import MoreFooter from './components/more-footer'
import mixin from '@/modules/mixin'
import CommentDialog from './components/CommentDialog'

var navigator = weex.requireModule('navigator')

export default {
  components: {PageLoadError, MoreFooter, CommentDialog},
  props: ['type'],
  mixins: [mixin],
  data () {
    return {
      loadingStatus: 'loading',
      messageList: [],
      showCommentDialog: false,
      currentCommentItem: null
    }
  },
  created () {
    this.getMessageList()
  },
  computed: {
    currentUserId () {
      return user.userId()
    }
  },
  methods: {
    getMessageList () {
      let self = this
      api.get('/edu/message/getPrivateMessageUser',
        {
          uid: user.userId()
        },
        function (data) {
          let array = data.content
          for (let i = 0; i < array.length; i++) {
            let item = array[i]
            if (item) {
              self.messageList.push(item)
            }
          }
          if (self.messageList.length === 0) {
            self.loadingStatus = 'no-data'
          } else {
            self.loadingStatus = 'no-more'
          }
        },
        function (e) {
          self.loadingStatus = 'error'
        }
      )
    },
    loadMore () {
      if (this.loadingStatus === 'no-more' || this.loadingStatus === 'no-data') return
      this.pageNo++
      this.getMessageList()
    },
    onDetail (item) {
      let url = api.getUri('private-message-detail') + '?uidother=' + ((item.sendFromId != this.currentUserId) ? item.sendFromId : item.sendToId)
      navigator.push({
        url: url,
        animated: true
      })
    }
    // commentSubmit (e) {
    //   console.log(this.currentCommentItem)
    //   let self = this
    //   api.post('/edu/message/addMessage', {
    //       sendToIds: this.currentCommentItem.sendFrom,
    //       sendFrom: user.userId(),
    //       content: e,
    //       type: this.type
    //     },
    //     function (data) {
    //       weex.requireModule('modal').toast({
    //         message: '回复成功',
    //         duration: 1
    //       })
    //       self.showCommentDialog = false
    //     },
    //     function (e) {
    //       weex.requireModule('modal').toast({
    //         message: e.msg,
    //         duration: 1
    //       })
    //     })
    // },
    // commentCancel () {
    //   this.showCommentDialog = false
    // }
  }
}
</script>

<style scoped>
    .list {
        background-color: rgb(240, 240, 240);
        flex: 1;
    }

    .system-cell {
        display: flex;
        flex-direction: column;
        border-radius: 10px;
    }

    .system-date {
        margin-top: 35px;
        width: 200px;
        margin-right: 10px;
        text-align: center;
        font-size: 24px;
        color: #969696;
    }

    .system-content {
        background-color: white;
        margin: 20px;
        padding: 20px;
        border: 1px solid #eee;
        box-shadow: 0px 0px 20px 0px rgba(26, 53, 140, 0.15);
    }

    .system-content-text {
        padding: 20px;
        font-size: 24px;
        color: #333333;
    }

    .personal-cell {
        display: flex;
        flex-direction: row;
        flex-wrap: wrap;
    }

    .cell-name-row {
        flex-direction: row;
    }

    .personal-cell-content {
        align-items: center;
        flex-wrap: wrap;
        flex-direction: row;
        margin: 20px;
        width: 710px;
        border: 1px solid #eee;
        border-radius: 10px;
        box-shadow: 0px 0px 20px 0px rgba(26, 53, 140, 0.15);
        background-color: white;
    }

    .header {
        margin-left: 30px;
        margin-top: 35px;
        margin-bottom: 35px;
        width: 80px;
        height: 80px;
        border-radius: 40px;
    }

    .personal-cell-right-content {
        display: flex;
        margin-left: 20px;
        flex-direction: column;
        flex: 1;
    }

    .personal-nick-name {
        flex: 1;
        margin-top: 30px;
        color: #000000;
        font-size: 24px;
    }

    .personal-content {
        margin-top: 10px;
        margin-bottom: 30px;
        font-size: 24px;
        color: #969696;
    }
</style>
