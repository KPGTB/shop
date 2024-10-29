import {redirect} from "react-router-dom"

const categoryLoader = async ({
	params,
}: {
	params: {
		category?: string
	}
}) => {
	const split = params.category?.split(".") || "-1"
	const id = split[split.length - 1]

	const res = await fetch(API_URL + "/category/" + id)
	if (res.status !== 200) return undefined
	const json = await res.json()

	let categoryData = json.data as CategoryDto
	let fixedUrl = categoryData.nameInUrl + "." + id

	if (params.category !== fixedUrl) {
		return redirect(`/shop/${fixedUrl}`)
	}

	return categoryData
}

export default categoryLoader
