import type { OrderProduct } from '../../types';

const PRODUCT_REGEX = /^(.+) \(x(\d+)\)$/;

export function parseProducts(raw: string[], nameMap: Map<string, string>): OrderProduct[] {
  return raw
    .map((s) => {
      const m = s.match(PRODUCT_REGEX);
      if (!m) return null;
      const productId = m[1];
      return { productId, name: nameMap.get(productId) ?? productId, quantity: parseInt(m[2], 10) };
    })
    .filter((p): p is OrderProduct => p !== null);
}
