const headerLoader = async () => {
	let controller = new AbortController()
	setTimeout(() => controller.abort(), 5000)

	const res = await fetch(`${API_URL}/category/list`, {
		signal: controller.signal,
	})
	if (res.status != 200) {
		return []
	}
	const json = await res.json()
	return json.data as Category[]
}

type Category = {
	id: number
	name: string
	description?: string
	nameInUrl: string
}

export type {Category}
export default headerLoader
