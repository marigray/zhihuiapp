<template>
    <div>
        <nav-bar show-back="true" title="添加课程调查问卷"></nav-bar>
        <scroller class="list">
            <div>
                <div class="questionnaire-name">
                    <text style="font-size:28px;color:#333333">问卷名称</text>
                    <input class="questionnaire-title" @input="inputQuestionTitle" v-model="title"></input>
                </div>
            </div>
            <div v-for="(item, index) in questions" :key="index">
                <div class="questionnaire-content">
                    <div class="question-cell">
                        <text>题目{{index+1}}</text>
                        <input class="option-title-input" v-model="item.name">
                    </div>

                    <div>
                        <div class="question-cell">
                            <text>单选题</text>
                            <div class="gap"></div>
                            <image class="select-btn" @click="typeSelected(item, 'radio')"
                                   :src="(item.type=='radio')?chooseOption.selectIcon:chooseOption.icon"></image>
                        </div>
                        <div v-if="item.type=='radio'">
                            <div v-for="(subItem, subIndex) in item.options" class="option"
                                 :key="subIndex">
                                <text class="option-title">选项{{subIndex+1}}</text>
                                <input class="option-title-input" v-model="subItem.content">
                            </div>
                        </div>
                    </div>

                    <div>
                        <div class="question-cell">
                            <text>多选题</text>
                            <div class="gap"></div>
                            <image class="select-btn" @click="typeSelected(item, 'checkbox')"
                                   :src="(item.type=='checkbox')?chooseOption.selectIcon:chooseOption.icon"></image>
                        </div>
                        <div v-if="item.type=='checkbox'">
                            <div v-for="(subItem, subIndex) in item.options" class="option"
                                 :key="subIndex">
                                <text class="option-title">选项{{subIndex+1}}</text>
                                <input class="option-title-input" v-model="subItem.content">
                            </div>
                        </div>
                    </div>

                    <div class="question-cell">
                        <text>简答题</text>
                        <div class="gap"></div>
                        <image class="select-btn" @click="typeSelected(item, 'text')"
                               :src="(item.type=='text')?chooseOption.selectIcon:chooseOption.icon"></image>
                    </div>
                </div>
                <image class="close-btn" :src="iconPath('icon_gb_big')" @click="deleteQuestion(index)"></image>
            </div>
            <div>
                <div class="next-btn" @click="addNextQuestion">
                    <text class="next-button">添加下一题</text>
                </div>
            </div>
            <div @click="postApi">
                <div class="complete-btn" style="background-color: rgb(15, 54, 145);">
                    <text class="complete-button">发布</text>
                </div>
            </div>
        </scroller>
    </div>
</template>

<script>
import NavBar from './components/nav-bar.vue'
import api from '@/modules/api'
import mixin from '@/modules/mixin'
import user from '@/modules/user'

var navigator = weex.requireModule('navigator')

export default {
  mixins: [mixin],
  components: {NavBar},
  data () {
    return {
      questions: [],
      title: '',
      type: 'add',
      id: 0
    }
  },
  computed: {
    chooseOption () {
      return {
        icon: this.iconPath('icon_choice'),
        selectIcon: this.iconPath('icon_choice_o')
      }
    }
  },
  created () {
    if (this.type !== 'add') {
      this.getQuestionnaire()
    }
  },
  methods: {
    addNextQuestion () {
      this.questions.push({
        name: '',
        type: 'radio',
        options: [{content: ''}, {content: ''}, {content: ''}, {content: ''}],
      })
    },
    inputQuestionTitle (e) {
      this.title = e.value
    },
    deleteQuestion (index) {
      this.questions.splice(index)
    },
    typeSelected (item, type) {
      item.type = type
      this.$forceUpdate()
    },
    getQuestionnaire () {
      let self = this
      api.get('/edu/survey/getSurveyDetail', {id: this.id},
        function (data) {
          self.id = data.content.id
          self.title = data.content.name
          self.questions = data.content.topics
        },
        function (e) {
        })
    },
    postApi () {
      if (this.type === 'add') {
        api.post1('/edu/survey/addSurvey', null, {uid: user.userId(), name: this.title, topics: this.questions},
          function (data) {
            weex.requireModule('modal').toast({
              message: '添加成功',
              duration: 1000
            })
            navigator.pop()
            weex.requireModule('myevent').sendGlobal('questionnaire-added', {id: data.content})
          },
          function () {
          })
      } else {
        api.post1('/edu/survey/updateSurvey', null,
          {
            id: this.id,
            uid: user.userId(),
            name: this.title,
            topics: this.questions
          },
          function (data) {
            weex.requireModule('modal').toast({
              message: '修改成功',
              duration: 1000
            })
            navigator.pop()
            // weex.requireModule('myevent').sendGlobal('questionnaire-changed', {})
          },
          function () {
          })
      }
    }
  }
}
</script>

<style scoped>
    .list {
        flex: 1;
        background-color: white;
    }

    .questionnaire-name {
        width: 690px;
        height: 90px;
        margin-left: 30px;
        margin-top: 20px;
        padding-left: 20px;
        padding-right: 20px;
        border-width: 1px;
        border-color: rgb(138, 130, 148);
        border-style: solid;
        border-radius: 5px;
        background-color: rgb(255, 255, 255);
        opacity: 0.502;
        flex-direction: row;
        align-items: center;
        justify-content: space-between;
    }

    .close-btn {
        width: 40px;
        height: 40px;
        position: absolute;
        top: 12px;
        right: 15px;
    }

    .questionnaire-content {
        margin-left: 30px;
        width: 690px;
        margin-top: 20px;
        margin-bottom: 20px;
        padding-left: 20px;
        padding-right: 20px;
        border-width: 1px;
        border-color: rgb(138, 130, 148);
        border-style: solid;
        border-radius: 5px;
        background-color: rgb(255, 255, 255);
        opacity: 0.502;
        align-items: center;
        justify-content: space-between;
    }

    .questionnaire-title {
        color: #0f3691;
        flex: 1;
        text-align: right;
        margin-left: 60px;
        font-size: 28px;
    }

    .question-cell {
        flex-direction: row;
        align-items: center;
        width: 650px;
        height: 90px;
        border-bottom-width: 1px;
        border-bottom-color: rgb(138, 130, 148);
        border-bottom-style: solid;
        align-items: center;
    }

    .option {
        width: 650px;
        height: 90px;
        border-style: solid;
        border-color: rgb(138, 130, 148);
        border-bottom-width: 1px;
        flex-direction: row;
        align-items: center;
    }

    .option-title {
        margin-left: 30px;
        font-size: 28px;
        color: #333;
        height: 50px;
        line-height: 50px;
    }

    .option-title-input {
        flex: 1;
        margin-left: 80px;
        font-size: 28px;
        text-align: right;
    }

    .select-btn {
        width: 40px;
        height: 40px;
        margin-right: 20px;
    }

    .gap {
        flex: 1;
    }

    .next-btn {
        margin-left: 20px;
        width: 710px;
        height: 90px;
        border-radius: 5px;
        margin-top: 40px;
        margin-bottom: 20px;
        justify-content: center;
        align-items: center;
        border-width: 1px;
        border-color: rgb(138, 130, 148);
        border-style: dashed;
        background-color: rgb(255, 255, 255);
    }

    .next-button {
        border-radius: 5px;
        color: rgb(15, 54, 145);
    }

    .complete-btn {
        margin-left: 20px;
        background-color: rgb(15, 54, 145);
        width: 710px;
        height: 90px;
        border-radius: 5px;
        margin-top: 40px;
        justify-content: center;
        align-items: center;
        margin-bottom: 20px;
    }

    .complete-button {
        font-size: 28px;
        color: #ffffff;
    }
</style>
