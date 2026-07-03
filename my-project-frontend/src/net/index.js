import axios from "axios"
import {ElMessage} from "element-plus"
import router from "@/router/index.js";


const authItemName = "access_token"

const defaultFailure=(message,code,url)=>{
    console.warn(`请求地址:${url},状态码${code},错误信息,${message}`)
    ElMessage.warning(message)
}
const defaultError=(err)=>{
    console.error(err)
    ElMessage.warning('发生了一些错误,请联系管理员')
}

function takeAccessToken(){
    const str = localStorage.getItem(authItemName)||sessionStorage.getItem(authItemName)
    if(!str) return  null
    const authObj = JSON.parse(str)
    if(authObj.expire<=new Date()){
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

function internalPost(url,data,header,success,failure,error=defaultError){
    axios.post(url,data,{headers:header}).then(({data})=>{
        if(data.code === 200){
            success(data.data)
        }else{
            failure(data.message,data.code,url)
        }
    }).catch(err=>error(err))
}
function internalGet(url, header, success, failure, error=defaultError){
    axios.get(url,{headers:header}).then(({data})=>{
        if(data.code === 200){
            success(data.data)
        }else{
            failure(data.message,data.code,url)
        }
    }).catch(err=>error(err))
}

function get(url,success,failure = defaultFailure){
    internalGet(url,accessHeader(),success,failure)
}
function post(url,data,success,failure = defaultFailure){
    internalPost(url,data,accessHeader(),success,failure)
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

let isRefreshing = false
axios.interceptors.response.use(
    response => response,
    async error => {
        const { config, response } = error
        if (response?.status === 401) {
            // 如果正在刷新 token，就等刷新结束后重试请求，不要直接跳转
            if (!isRefreshing) {
                isRefreshing = true
                try {
                    // 尝试用 refresh token 刷新 access token（如果有的话）
                    // const newToken = await refreshAccessToken()
                    // 若刷新成功，更新本次请求的 token 并重试
                    // config.headers.Authorization = `Bearer ${newToken}`
                    // return request(config)
                    // 若无刷新逻辑，直接清除并跳转
                    deleteAccessToken()
                    router.push('/')
                    ElMessage.error('登录已失效，请重新登录')
                } finally {
                    isRefreshing = false
                }
            }
        }
        return Promise.reject(error)
    }
)

export {login,logout,get,post,isUnauthorized,takeAccessToken,accessHeader,isRoleAdmin}