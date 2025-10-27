import { faker } from '@faker-js/faker';
import fs from 'fs';

const ITEMS = 20;
const items = Array.from({ length: ITEMS }).map((_, index) => ({
  id: faker.string.uuid(),
  name: faker.commerce.productName(),
  type: faker.helpers.arrayElement(['Shirt', 'Pants', 'Shoes', 'Accessory', 'Outerwear', 'Other'] as const),
  color: faker.color.human(),
  brand: faker.company.name(),
  size: faker.helpers.arrayElement(['XS', 'S', 'M', 'L', 'XL']),
  tags: faker.helpers.arrayElements(['Formal', 'Casual', 'Winter', 'Summer', 'Sport'], 2),
  notes: faker.commerce.productDescription(),
  photoURL: null,
  nfcUID: faker.string.hexadecimal({ length: 14 }).toUpperCase(),
  createdAt: Date.now() - index * 1000 * 60 * 60,
}));

fs.writeFileSync('mock-items.json', JSON.stringify(items, null, 2));
console.log('Generated mock-items.json');
