const businessProductsLoader = async () => {
	const categoriesRes = await fetch(`${API_URL}/category/list/full`)
	if (categoriesRes.status !== 200) return []
	const categoriesJson = await categoriesRes.json()

	const taxes: TaxCode[] = []
	let hasMore = true
	let page = 1

	while (hasMore) {
		const res = await fetch(`${API_URL}/product/taxes?page=${page}`)
		if (res.status !== 200) break
		const json = await res.json()
		hasMore = json.data.hasMore
		json.data.codes.forEach((code: TaxCode) => taxes.push(code))
		page++
	}

	return [categoriesJson.data as FullCategory[], taxes]
}

type TaxCode = {
	id: string
	name: string
	description: string
}

type FullCategory = {
	id: number
	name: string
	description: string
	nameInUrl: string
	products: Product[]
}

type Product = {
	id: number
	name: string
	nameInUrl: string
	description: string
	image: string
	currency: string
	price: number
	taxCode: string
	categoryId: number
	fields: Field[]
}

type Field = {
	id?: number
	label: string
	optional: boolean
	type: "DROPDOWN" | "NUMERIC" | "TEXT"
	options: DropdownOption[]
}

type DropdownOption = {
	id?: number
	label: string
	value: string
}

export type {FullCategory, Product, Field, DropdownOption, TaxCode}
export default businessProductsLoader
