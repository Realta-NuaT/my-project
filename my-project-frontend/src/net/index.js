import axios from "axios"
import {ElMessage} from "element-plus"
import router from "@/router/index.js";


const authItemName = "access_token"

const defaultFailure=(message,code,url)=>{
    console.warn(`请求地址:${url},状态码${code},错误信息,${message}`)
    ElMessage.warning(message)
}
const defaultError = async (message, err) => {
    console.error(message?message:'无信息' ,err)

    if (err.response?.status === 401) {
        deleteAccessToken()

        ElMessage.error(message ? message : '登录已失效，请重新登录')

        if (router.currentRoute.value.path !== '/') {
            await router.push('/')
        }

        return
    }

    ElMessage.warning('发生了一些错误，请联系管理员')
}

function takeAccessToken(){
    const str = localStorage.getItem(authItemName)||sessionStorage.getItem(authItemName)
    if(!str) return  null
    const authObj = JSON.parse(str)
    if(new Date(authObj.expire) <= Date.now()){
        deleteAccessToken(authObj)
        ElMessage.warning('登录状态已过期,请重新登录')
        return null
    }
    return authObj
}
function storeAccessToken(token,remember,expire,role){
    const authObj={token:token, expire:expire, role:role};
    const str = JSON.stringify(authObj)
    if(remember){
        localStorage.setItem(authItemName,str)
    }else{
        sessionStorage.setItem(authItemName,str)
    }
}
function deleteAccessToken(){
    localStorage.removeItem(authItemName)
    sessionStorage.removeItem(authItemName)
}
function accessHeader(){
    const token = takeAccessToken()
    return token?{'Authorization': `Bearer ${takeAccessToken()?.token}`}:{}
}

function internalPost(
    url,
    data,
    header,
    success,
    failure,
    error = defaultError
) {
    axios.post(url, data, {headers: header})
        .then(({data}) => {

            // 后端返回 401
            if (data.code === 401) {
                return error(data.message,{
                    response: {
                        status: 401
                    }
                })
            }

            if (data.code === 200) {
                success(data.data)
            } else {
                failure(data.message, data.code, url)
            }
        })
        .catch(err => error(data.message, err))
}
function internalGet(url, header, success, failure, error = defaultError) {
    axios.get(url, {headers: header})
        .then(({data}) => {

            // 后端返回 401
            if (data.code === 401) {
                return error(data.message,{
                    response: {
                        status: 401
                    }
                })
            }

            if (data.code === 200) {
                success(data.data)
            } else {
                failure(data.message, data.code, url)
            }
        })
        .catch(err => error(data.message,err))
}

function get(url,success,failure = defaultFailure){
    internalGet(url,accessHeader(),success,failure)
}
function post(url,data,success,failure = defaultFailure){
    internalPost(url,data,accessHeader(),success,failure)
}
function fetchPost(url,data){
    return fetch(axios.defaults.baseURL + url,{
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${takeAccessToken()?.token}`
        },
        body: JSON.stringify(data)
    })
}

function  login(username,password,remember,success,failure=defaultFailure){
    internalPost('api/auth/login',{
        username:username,
        password:password,
    },{
        'content-type': 'application/x-www-form-urlencoded',
    },(data)=>{
        storeAccessToken(data.token,remember,data.expireTime, data.role)
        ElMessage.success(`登录成功,欢迎${data.username}来到我们的系统`)
        success(data.data)
    },failure)
}
function logout(success,failure = defaultFailure){
    get('/api/auth/logout',()=>{
        deleteAccessToken()
        ElMessage.success('退出登录成功,欢迎您再次使用')
        success()
    },failure)
}

function isUnauthorized(){
    return !takeAccessToken()
}

function isRoleAdmin(){
    return takeAccessToken()?.role === 'admin'
}

export {login,logout,get,post,isUnauthorized,takeAccessToken,accessHeader,isRoleAdmin,fetchPost}