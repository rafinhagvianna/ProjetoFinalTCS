import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CadastroSetorComponent } from './cadastro-setor.component';

describe('CadastroSetorComponent', () => {
  let component: CadastroSetorComponent;
  let fixture: ComponentFixture<CadastroSetorComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CadastroSetorComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CadastroSetorComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
