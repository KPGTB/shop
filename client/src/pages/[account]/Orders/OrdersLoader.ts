import countriesJson from "../../../assets/countries.json"

const countries = countriesJson as {
	[code: string]: string
}

const ordersLoader = async (res: Response) => {
	if (res.status !== 200) return []
	const json = await res.json()
	const result = json.data as OrderDto[]
	result.forEach((order) => {
		order.country = countries[order.country || ""] as string
	})
	return result.sort((o1, o2) => o2.id - o1.id)
}

const ownOrdersLoader = async () => {
	const res = await fetch(API_URL + "/user/orders", {credentials: "include"})
	const result = await ordersLoader(res)
	return result
}

const allOrdersLoader = async () => {
	const res = await fetch(API_URL + "/order/all", {credentials: "include"})
	const result = await ordersLoader(res)
	return result
}

export {ownOrdersLoader, allOrdersLoader}
