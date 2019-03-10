var stream = weex.requireModule('stream')
// var BaseUrl = 'http://116.62.23.7:3060'
var BaseUrl = 'http://zh.zhihui-app.com'

export default {
  getBaseUrl () {
    return BaseUrl
  },
  get (path, parameter, success, failed) {
    let url = BaseUrl + path
    url = url + '?s=133333'
    for (let k in parameter) {
      url += '&' + encodeURIComponent(k) + '=' + encodeURIComponent(parameter[k])
    }
    let options = {
      url: url,
      method: 'GET',
      type: 'json'
    }
    options.headers = {'Content-Type': 'application/x-www-form-urlencoded'}
    // weex.requireModule('user').JSLog(url)
    stream.fetch(options, function (response) {
      // weex.requireModule('user').JSLog(response)
      if (response.status === 200) {
        let data = response.data
        if (data && data.httpCode === 200) {
          if (success) {
            success(data)
          }
        } else {
          if (failed) failed(data)
        }
      } else {
        if (failed) failed(response)
      }
    })
  },
  // sb接口，URL部分参数，body部分参数
  post1 (path, parameter, body, success, failed) {
    let url = BaseUrl + path
    url = url + '?s=133333'
    for (let k in parameter) {
      url += '&' + encodeURIComponent(k) + '=' + encodeURIComponent(parameter[k])
    }
    let options = {
      url: url,
      method: 'POST',
      type: 'json'
    }
    options.body = JSON.stringify(body)
    options.headers = {'Content-Type': 'application/json'}
    // options.body = JSON.stringify(parameter)
    // for (let k in parameter) {
    //   options.body += '&' + encodeURIComponent(k) + '=' + encodeURIComponent(parameter[k])
    // }
    // weex.requireModule('user').JSLog(url)
    stream.fetch(options, function (response) {
      // weex.requireModule('user').JSLog(response)
      if (response.status === 200) {
        let data = response.data
        if (data && data.httpCode === 200) {
          if (success) {
            success(data)
          }
        } else {
          if (failed) failed(data)
        }
      } else {
        if (failed) failed(response)
      }
    })
  },
  post (path, parameter, success, failed) {
    let url = BaseUrl + path
    url = url + '?s=133333'
    for (let k in parameter) {
      url += '&' + encodeURIComponent(k) + '=' + encodeURIComponent(parameter[k])
    }
    let options = {
      url: url,
      method: 'POST',
      type: 'json'
    }
    // options.body = ''
    options.headers = {'Content-Type': 'application/json'}
    // options.body = JSON.stringify(parameter)
    // for (let k in parameter) {
    // options.body += '&' + k + '=' + parameter[k]
    // options.body += '&' + encodeURIComponent(k) + '=' + encodeURIComponent(parameter[k])
    // }
    weex.requireModule('user').JSLog(url)
    stream.fetch(options, function (response) {
      weex.requireModule('user').JSLog(response)
      if (response.status === 200) {
        let data = response.data
        if (data && data.httpCode === 200) {
          if (success) {
            success(data)
          }
        } else {
          if (failed) failed(data)
        }
      } else {
        if (failed) failed(response)
      }
    })
  },
  getUri (link) {
    return weex.config.env.scheme + '://' + link
  }
}
