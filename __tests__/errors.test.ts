import { getErrorMessage } from '@/utils/errors';

describe('getErrorMessage', () => {
  it('maps NFC duplicate error', () => {
    expect(getErrorMessage('NFCTAG_DUPLICATE')).toContain('already linked');
  });
});
