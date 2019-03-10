<template>
    <div class="bg">
        <div class="content">
            <text class="title">{{title}}</text>
            <textarea class="text-area" @input="input"></textarea>
            <div class="btn-container">
                <text class="cancel" @click="cancel">取消</text>
                <div class="gap"></div>
                <text class="submit" @click="comment">提交</text>
            </div>
        </div>
    </div>
</template>

<script>
export default {
  props: ['title'],
  name: 'CommentDialog',
  data () {
    return {text: null}
  },
  methods: {
    comment () {
      if (!this.text) {
        weex.requireModule('modal').toast({
          message: '内容不能为空',
          duration: 1
        })
        return
      }
      this.$emit('commentSubmit', this.text)
    },
    cancel () {
      this.$emit('commentCancel')
    },
    input (e) {
      this.text = e.value
    }
  }
}
</script>

<style scoped>
    .bg {
        position: fixed;
        top: 0;
        bottom: 0;
        left: 0;
        right: 0;
        background-color: rgba(0, 0, 0, 0.5);
        justify-content: center;
        align-items: center;
    }

    .content {
        width: 675px;
        height: 620px;
        background-color: white;
        border-radius: 10px;
        align-items: center;
    }

    .text-area {
        width: 590px;
        height: 300px;
        border-color: rgb(200, 200, 200);
        border-width: 1px;
        margin-top: 50px;
    }

    .title {
        margin-top: 40px;
        font-size: 32px;
        color: black;
    }

    .btn-container {
        margin-top: 50px;
        flex-direction: row;
    }

    .cancel {
        width: 280px;
        background-color: rgb(230, 230, 230);
        height: 80px;
        text-align: center;
        line-height: 80px;
        color: rgb(88, 88, 88);
        font-size: 30px;
    }

    .gap {
        width: 30px;
    }

    .submit {
        background-color: rgb(6, 58, 149);
        width: 280px;
        height: 80px;
        color: white;
        text-align: center;
        line-height: 80px;
        font-size: 30px;
    }

</style>

