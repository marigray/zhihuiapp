<template>
    <div>
        <nav-bar show-back="true" title="调查问卷"></nav-bar>
        <list class="list">
            <cell>
                <div class="questionnaire-name">
                    <text style="font-size:28px;color:#333333">问卷名称</text>
                    <input class="questionnaire-title" :disabled="true" :value="title"></input>
                </div>
            </cell>
            <cell v-for="(item, index) in questions" :key="index">
                <div class="questionnaire-content">
                    <div class="question-cell">
                        <text>题目{{index+1}}</text>
                        <input class="option-title-input questionnaire-title" :disabled="true" :value="item.name">
                    </div>
                    <div class="question-cell" v-if="item.type=='radio'">
                        <text>单选题</text>
                        <div class="gap"></div>
                    </div>
                    <div v-if="item.type=='radio'" v-for="(subItem, subIndex) in item.options" class="option"
                         :key="subIndex" @click="answerOption(item, subItem)">
                        <text class="option-title" @click="answerOption(item, subItem)">{{subItem.content}}</text>
                        <div class="gap" @click="answerOption(item, subItem)"></div>
                        <image class="select-btn"
                               :src="subItem.isSelected?chooseOption.selectIcon:''"
                               @click="answerOption(item, subItem)"></image>
                    </div>
                    <div class="question-cell" v-if="item.type=='checkbox'">
                        <text>多选题</text>
                        <div class="gap"></div>
                    </div>
                    <div v-if="item.type=='checkbox'" v-for="(subItem, subIndex) in item.options" class="option"
                         :key="subIndex" @click="answerOption1(item, subItem)">
                        <text class="option-title" @click="answerOption1(item, subItem)">{{subItem.content}}</text>
                        <div class="gap" @click="answerOption1(item, subItem)"></div>
                        <image class="select-btn"
                               :src="subItem.isSelected?chooseOption.selectIcon:''"
                               @click="answerOption1(item, subItem)"></image>
                    </div>
                    <div class="question-cell" v-if="item.type=='text'">
                        <text>简答题</text>
                        <div class="gap"></div>
                    </div>
                    <textarea class="textarea" v-if="item.type=='text'" v-model="item.answer"></textarea>
                </div>
            </cell>
            <cell @click="submit">
                <div class="complete-btn" style="background-color: rgb(15, 54, 145);">
                    <text class="complete-button">提交</text>
                </div>
            </cell>
        </list>
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
      questions: [
          {
              name: "问卷1129",
              type: "radio",
              options: [
                  {
                      id: 113,
                      createBy: 0,
                      createDate: null,
                      updateBy: 0,
                      updateDate: null,
                      remark: null,
                      delFlag: 0,
                      content: "hkkhkjk",
                      option: null,
                      topicid: 38,
                      isSelected:true
                  }
              ]
          },
          {
              name: "问卷29",
              type: "checkbox",
              options: [
                  {
                      id: 123,
                      createBy: 0,
                      createDate: null,
                      updateBy: 0,
                      updateDate: null,
                      remark: null,
                      delFlag: 0,
                      content: "",
                      option: null,
                      topicid: 38,
                      isSelected:true
                  }
              ]
          },
          {
              name: "问卷22",
              type: "text",
              options: [
                  {
                      id: 123,
                      createBy: 0,
                      createDate: null,
                      updateBy: 0,
                      updateDate: null,
                      remark: null,
                      delFlag: 0,
                      content: "",
                      option: null,
                      topicid: 38
                  }
              ]
          }
      ],
      title: '',
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
    this.getQuestionnaire()
  },
  methods: {
    answerOption (item, subItem) {
      item.topicId = subItem.topicid
      item.answer = subItem.id
        console.log(item.topicId)
        console.log(item.answer)
      for (let i = 0; i < item.options.length; i++) {
        let t = item.options[i]
        t.isSelected = false
      }
      subItem.isSelected = true
    },
    answerOption1 (item, subItem) {
      item.topicId = subItem.topicid
      subItem.isSelected = !subItem.isSelected
      // TODO 移除
      item.answer.push(subItem.id)
    },
    inputQuestionTitle (e) {
      this.title = e.value
    },
    deleteQuestion (index) {
      this.questions.splice(index)
    },
    typeSelected (item, type) {
      item.type = type
      if (type === 'checkbox') {
        item.answer = []
      } else {
        item.answer = ''
      }
    },
    getQuestionnaire () {
      let self = this
      api.get('/edu/survey/getSurveyDetail', {id: 94},
        function (data) {
          self.id = data.content.id
          self.title = data.content.name
          let array = data.content.topics
          for (let i = 0; i < array.length; i++) {
            let item = array[i]
            item.answer = item.type === 'checkbox' ? [] : ''
            for (let j = 0; j < item.options.length; j++) {
              let t = item.options[j]
              t.isSelected = false
              item.topicId = t.topicid
            }
          }
          self.questions = array
        console.log(self.questions)
        },
        function (e) {
        })
    },
    submit () {
      let self = this
      let data = []
      for (let i = 0; i < this.questions.length; i++) {
        let item = this.questions[i]
        let result = {}
        result.surveyId = this.id
        result.uid = user.userId()
        result.topicId = item.topicId
        result.answer = item.answer
        if (item.type === 'checkbox') {
          result.answer = item.answer.join(',')
        }
        data.push(result)
      }
      weex.requireModule('user').JSLog(data)
      api.post1('/edu/survey/addUserAnswer', null, data,
        function (data) {
          weex.requireModule('modal').toast({
            message: '提交问卷成功',
            duration: 1000
          })
          weex.requireModule('myevent').sendGlobal('survey-answered', {id: self.id})
          navigator.pop()
        },
        function (e) {
          weex.requireModule('modal').toast({
            message: e.msg,
            duration: 1000
          })
        })
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
        height: 90px;
        width: 650px;
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

    .textarea {
        padding: 20px;
        width: 710px;
        height: 200px;
        margin-left: 20px;
    }
</style>
