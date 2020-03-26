import { TestBed } from '@angular/core/testing';

import { ConsentAuthService } from './consent-auth.service';
import { HttpClientTestingModule } from '@angular/common/http/testing';

describe('ConsentAuthService', () => {
  beforeEach(() =>
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule]
    })
  );

  it('should be created', () => {
    const service: ConsentAuthService = TestBed.get(ConsentAuthService);
    expect(service).toBeTruthy();
  });
});
