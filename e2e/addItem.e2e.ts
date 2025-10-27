describe('TagTrack Add Item flow', () => {
  beforeAll(async () => {
    await device.launchApp({ delete: true, permissions: { camera: 'YES', photos: 'YES' } });
  });

  it('shows onboarding', async () => {
    await expect(element(by.text('TagTrack'))).toBeVisible();
  });

  it('navigates to add item from home', async () => {
    await element(by.text('Add Item')).tap();
    await expect(element(by.text('Tap an NFC tag to start'))).toBeVisible();
  });
});
