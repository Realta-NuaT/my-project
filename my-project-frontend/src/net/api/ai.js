import {fetchPost} from "@/net";

export const apiChatWithAi = async (
    context,
    onMessage,
    onError,
    onComplete
) => {
    try {
        const response = await fetchPost("/api/ai/chat", context)

        if (!response.ok) {
            throw new Error(`HTTP ${response.status}`)
        }

        const reader = response.body.getReader()
        const decoder = new TextDecoder()

        let buffer = ""

        while (true) {
            const {done, value} = await reader.read()

            if (done) break

            buffer += decoder.decode(value, {stream: true})

            // SSE 一个完整事件以两个换行结束
            const events = buffer.split("\n\n")

            // 最后一段可能还没接收完整，留下继续等待
            buffer = events.pop()

            for (const event of events) {

                const lines = event.split("\n")

                const content = lines
                    .filter(line => line.startsWith("data:"))
                    .map(line => line.substring(5).trimStart())
                    .join("\n")

                if (content) {
                    onMessage(content)
                }
            }
        }

        // 处理最后剩余的数据
        if (buffer) {
            const content = buffer
                .split("\n")
                .filter(line => line.startsWith("data:"))
                .map(line => line.substring(5).trimStart())
                .join("\n")

            if (content) {
                onMessage(content)
            }
        }

        onComplete()

    } catch (e) {
        onError(e)
    }
}