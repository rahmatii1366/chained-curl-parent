import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { ChangeDetectionStrategy, Component, OnInit, viewChild } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { provideNativeDateAdapter } from '@angular/material/core';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatAccordion, MatExpansionModule } from '@angular/material/expansion';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { ChainedCurlDto, ChainedCurlResponseDto, ChainedCurlServerConfig, FlowDto, InitialValuesDto } from '../../dto/chained-curl-dto';
import { BehaviorSubject, Observable } from 'rxjs';
import { FormBuilder, FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';

@Component({
  selector: 'app-chained-curl',
  standalone: true,
  providers: [
    provideNativeDateAdapter()
  ],
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatButtonModule,
    MatExpansionModule,
    MatIconModule,
    MatFormFieldModule,
    MatInputModule,
    MatDatepickerModule
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
  templateUrl: './chained-curl.component.html',
  styleUrl: './chained-curl.component.css'
})
export class ChainedCurlComponent implements OnInit {
  accordion = viewChild.required(MatAccordion);
  curlConf: any = null;
  formMap: Map<string, FormGroup> = new Map<string, FormGroup>();

  constructor(private httpClient: HttpClient,
    private formBuilder: FormBuilder
  ) {
  }

  ngOnInit(): void {
    // this.httpClient.get("/api/v1/chained-curl/test").subscribe(res => console.log(res));

    // this.getCurlConf().subscribe(res => {
    //   this.curlConf = res;
    // }, err => {
    //   console.log(err);
    // });
  }

  // getCurlConf(): Observable<object> {
  // return this.httpClient.post("/api/v1/chained-curl/test/create-context", "
  // ")
  // }

  private _chainedCurlDto: ChainedCurlServerConfig = null;
  private _chainedCurlDto$: BehaviorSubject<ChainedCurlServerConfig> = new BehaviorSubject(this._chainedCurlDto);

  get chainedCurlDto$(): Observable<ChainedCurlServerConfig> {
    return this._chainedCurlDto$.asObservable();
  }

  get chainedCurlDto(): ChainedCurlServerConfig {
    return this._chainedCurlDto;
  }

  set chainedCurlDto(_chainedCurlDto: ChainedCurlServerConfig) {
    this._chainedCurlDto = _chainedCurlDto;
    // console.log(JSON.stringify(this._chainedCurlDto));
    // console.log(JSON.stringify(this._chainedCurlDto.chainedCurlDto.chain));
    // this._chainedCurlDto.chainedCurlDto.chain.forEach((value: FlowDto, key: string) => {
    //   this.startStep(key, value);
    // });

    Object.keys(this._chainedCurlDto.chainedCurlDto.chain).forEach(key => {
      // if (this._chainedCurlDto.chainedCurlDto.chain[key] &&
      //   this._chainedCurlDto.chainedCurlDto.chain[key].provide &&
      //   this._chainedCurlDto.chainedCurlDto.chain[key].provide.input)
      //   this.startStep(key, this._chainedCurlDto.chainedCurlDto.chain[key]);
      this.startStep(key, this._chainedCurlDto.chainedCurlDto.chain[key]);
    });

    this._chainedCurlDto$.next(this._chainedCurlDto);
  }

  private _chainedCurlResponseMap: Map<string, ChainedCurlResponseDto> = new Map<string, ChainedCurlResponseDto>();
  private _chainedCurlResponseMap$: BehaviorSubject<Map<string, ChainedCurlResponseDto>> = new BehaviorSubject(this._chainedCurlResponseMap);
  private _chainedCurlResponseMapChange: number = 0;
  private _chainedCurlResponseMapChange$: BehaviorSubject<number> = new BehaviorSubject(this._chainedCurlResponseMapChange);

  get chainedCurlResponseMapChanged$(): Observable<number> {
    return this._chainedCurlResponseMapChange$.asObservable();
  }

  get chainedCurlResponseMap$(): Observable<Map<string, ChainedCurlResponseDto>> {
    return this._chainedCurlResponseMap$.asObservable();
  }

  setChainedCurlResponseMap(stepName: string, _chainedCurlResponseDto: ChainedCurlResponseDto) {
    console.log(stepName, JSON.stringify(_chainedCurlResponseDto))
    this._chainedCurlResponseMap.set(stepName, _chainedCurlResponseDto);
    console.log(this._chainedCurlResponseMap)
    // var newMap = {};
    // for (var i in this._chainedCurlResponseMap)
    //   newMap[i] = this._chainedCurlResponseMap[i];
    var newMap = new Map(this._chainedCurlResponseMap)
    this._chainedCurlResponseMap$.next(newMap);
    this._chainedCurlResponseMapChange = this._chainedCurlResponseMapChange + 1;
    this._chainedCurlResponseMapChange$.next(this._chainedCurlResponseMapChange)
  }

  getChainedCurlResponseMap(stepName: string): ChainedCurlResponseDto {
    if (this._chainedCurlResponseMap.has(stepName))
      return this._chainedCurlResponseMap.get(stepName);
    return null;
  }

  onFileSelected(event: any) {

    const file: File = event.target.files[0];

    if (file) {
      const formData = new FormData();
      formData.append("ChainedCurlConfig", file);
      this.httpClient.post<ChainedCurlServerConfig>("/api/v1/chained-curl/test/upload-and-get-curl-config", formData, { observe: 'response' }).subscribe(
        res => {
          this.chainedCurlDto = res.body;
        }, err => {

        }
      );
    }
  }

  asIsOrder(a, b) {
    return 1;
  }

  fetchContext(contextId: string) {
    this.httpClient.get<InitialValuesDto>("/api/v1/chained-curl/test/evaluated-initial-values?context-id=" + contextId, { observe: 'response' })
  }

  startStep(stepName: string, flow: FlowDto) {
    if (this.formMap.has(stepName)) {
      console.log("step exists!")
    } else {
      let group = this.formBuilder.group({
      });

      if (flow &&
        flow.provide &&
        flow.provide.input)
        Object.keys(flow.provide.input).forEach(key => {
          group.addControl(key, new FormControl(''));
        });

      this.formMap.set(stepName, group);
    }
  }

  getFormGroup(stepName: string): FormGroup {
    console.log(stepName, this.formMap.get(stepName))
    return this.formMap.get(stepName);
  }

  submitForm(stepName: string) {
    console.log(stepName + ' submitted!')
    console.log(this.formMap.get(stepName));
    if (this.formMap.get(stepName))
      console.log(this.formMap.get(stepName).value);

    this.httpClient.post<ChainedCurlResponseDto>("/api/v1/chained-curl/test/do-curl?context-id=" + this._chainedCurlDto.contextId + "&step-name=" + stepName,
      this.formMap.get(stepName).value,
      { observe: 'response' }).subscribe(
        res => {
          console.log(res.body);
          this.setChainedCurlResponseMap(stepName, res.body);
        }, err => {

        }
      );
  }

  syntaxHighlight(json) {
    if (typeof json != 'string') {
      json = JSON.stringify(json, undefined, 2);
    }
    json = json.replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;');
    return json.replace(/("(\\u[a-zA-Z0-9]{4}|\\[^u]|[^\\"])*"(\s*:)?|\b(true|false|null)\b|-?\d+(?:\.\d*)?(?:[eE][+\-]?\d+)?)/g, function (match) {
      var cls = 'number';
      if (/^"/.test(match)) {
        if (/:$/.test(match)) {
          cls = 'key';
        } else {
          cls = 'string';
        }
      } else if (/true|false/.test(match)) {
        cls = 'boolean';
      } else if (/null/.test(match)) {
        cls = 'null';
      }
      return '<span class="' + cls + '">' + match + '</span>';
    });
  }

  stringify2(s): string {
    return this.syntaxHighlight(JSON.parse(s))
  }

  stringify(s): string {
    return JSON.stringify(JSON.parse(s), null, 2)
  }

  stringify3(s): string {
    return JSON.stringify(s)
  }
}