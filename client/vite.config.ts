import {defineConfig, loadEnv} from "vite"

import react from "@vitejs/plugin-react-swc"

// https://vitejs.dev/config/
export default defineConfig(({mode}) => {
	const env = loadEnv(mode, process.cwd(), "")
	return {
		define: {
			API_URL: JSON.stringify(env.API_URL),
			BUSINESS_ACCOUNT_EMAIL: JSON.stringify(env.BUSINESS_ACCOUNT_EMAIL),
			CAPTCHA_KEY: JSON.stringify(env.CAPTCHA_KEY),
		},
		plugins: [react()],
	}
})
