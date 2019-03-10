<template>
    <div class="wrapper">
        <nav-bar show-back="true" title="发票管理"></nav-bar>
        <list class="list">
            <cell class="cell" v-for="(item,index) in invoicelist" :key="index">
                <div class="cell-content" @click="onCellSelected(item)">
                    <div class="subrow">
                        <text class="title">发票抬头：</text>
                        <text class="content">{{item.title}}</text>
                    </div>
                    <div class="btn-div" v-if="!isInSelectionMode">
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
        </list>
        <div class="bottom">
            <div class="btn" @click="onNavigator('invoice2')">
                <text style="font-size: 28px;color: #ffffff;">添加发票信息</text>
            </div>
        </div>
        <PopupMenu v-if="isPopupShow" @popupClickedWithBtnIndex="popupClickedWithBtnIndex" contentInfo="您确认要删除该发票吗"
                   iconImage="icon_gth" :statusType="true"></PopupMenu>
    </div>
</template>

<script>
import NavBar from './components/nav-bar.vue'
import user from '@/modules/user'
import mixin from '@/modules/mixin'
import api from '@/modules/api'
import PopupMenu from './components/popup-menu'

var navigator = weex.requireModule('navigator')

export default {
  mixins: [mixin],
  components: {NavBar, PopupMenu},
  data () {
    return {
      invoicelist: [],
      isInSelectionMode: false,
      isPopupShow: false,
      orderid: 0
    }
  },
  created () {
    this.getInvoices()
    var globalEvent = weex.requireModule('globalEvent');
    globalEvent.addEventListener('invoice-add', function (e) {
      this.getInvoices()
    }.bind(this))
  },
  methods: {
    onNavigator (link) {
      navigator.push({
        url: api.getUri(link),
        animated: true
      })
    },
    getInvoices () {
      let self = this
      api.get('/edu/invoice/getInvoiceTitleListByUser', {uid: user.userId()},
        function (data) {
          self.invoicelist = data.content
        },
        function () {

        })
    },
    onCellSelected (item) {
      if (this.isInSelectionMode) {
        weex.requireModule('myevent').sendGlobal('invoice-selected', {
          id: item.id,
          orderid: this.orderid,
          titleName: encodeURIComponent(item.title)
        })
        navigator.pop()
        return
      }
      navigator.push({
        url: api.getUri('invoice2') + '?type=edit' + '&id=' + item.id,
        animated: true
      })
    },
    onDelete (item) {
      this.currentItem = item
      this.isPopupShow = true
    },
    popupClickedWithBtnIndex (index) {
      let arrayIndex = -1
      for (var i = 0; i < this.invoicelist.length; i++) {
        if (this.invoicelist[i] === this.currentItem) arrayIndex = i
      }
      this.isPopupShow = false
      if (arrayIndex === -1) {
        return
      }

      if (index === 1) {
        let self = this
        api.post('/edu/invoice/delInvoiceTitle', {id: this.currentItem.id},
          function (data) {
            self.invoicelist.splice(arrayIndex, 1)
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
    .wrapper {
        background-color: rgb(240, 240, 240);
    }

    .list {
        flex: 1;
    }

    .cell {
    }

    .cell-content {
        height: 130px;
        margin: 20px;
        box-shadow: 0px 0px 20px 0px rgba(26, 53, 140, 0.15);
        align-items: flex-start;
        justify-content: space-around;
        background-color: white;
    }

    .subrow {
        width: 650px;
        margin-left: 30px;
        margin-right: 30px;
        margin-top: 30px;
        flex-direction: row;
        align-items: center;
        justify-content: flex-start;
    }

    .title {
        font-size: 28px;
        color: #333333;
    }

    .content {
        font-size: 28px;
        color: #0f3691;
    }

    .bottom {
        flex-direction: row;
        justify-content: center;
    }

    .btn {
        margin-top: 45px;
        margin-bottom: 45px;
        /*margin-left: 20px;*/
        width: 710px;
        height: 90px;
        border-radius: 5px;
        background-color: rgb(15, 54, 145);
        justify-content: center;
        align-items: center;
        font-size: 28px;
        color: #ffffff;
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
