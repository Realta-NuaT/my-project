import {get} from "@/net";

export const apiEmailRecord  = (success) =>
    get('/api/admin/email/list',success)
