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
import { ChainedCurlDto, ChainedCurlResponseDto, ChainedCurlServerConfig, FlowDto, InitialValuesDto, TabChainedCurlContainer } from '../../dto/chained-curl-dto';
import { BehaviorSubject, Observable } from 'rxjs';
import { FormBuilder, FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { MatTabsModule } from '@angular/material/tabs';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatMenuModule } from '@angular/material/menu';
import { MatTooltipModule } from '@angular/material/tooltip';
import { ReturnStatement } from '@angular/compiler';
import { CollectionObservableService } from '../../service/collection-observable.service';

@Component({
  selector: 'app-chained-curl-tab',
  standalone: true,
  providers: [
    provideNativeDateAdapter(),
    CollectionObservableService
  ],
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatButtonModule,
    MatExpansionModule,
    MatIconModule,
    MatFormFieldModule,
    MatInputModule,
    MatDatepickerModule,
    MatTabsModule,
    MatMenuModule,
    MatCheckboxModule,
    MatTooltipModule
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
  templateUrl: './chained-curl-tab.component.html',
  styleUrl: './chained-curl-tab.component.css'
})
export class ChainedCurlTabComponent implements OnInit {
  accordion = viewChild.required(MatAccordion);
  curlConf: any = null;
  formMap: Map<string, Map<string, FormGroup>> = new Map<string, Map<string, FormGroup>>();

  constructor(private httpClient: HttpClient,
    private formBuilder: FormBuilder, 
    private collectionObservableService: CollectionObservableService
  ) {
  }

  ngOnInit(): void {
    this.httpClient.get("/chained-curl/list").subscribe(res => {
      this.chainedCurlList = res;
      // console.log(res);
    });

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

  private _expandStepMap: Map<string, boolean> = new Map();

  private _tabChainedCurls: Map<string, TabChainedCurlContainer> = new Map();
  private _tabChainedCurls$: BehaviorSubject<Map<string, TabChainedCurlContainer>> = new BehaviorSubject(this._tabChainedCurls);
  private _selected: TabChainedCurlContainer = null;
  private _tabCounter: number = 0;
  private _tabSelectedIndex: number = 0;

  get tabChainedCurls$(): Observable<Map<String, TabChainedCurlContainer>> {
    return this._tabChainedCurls$.asObservable();
  }

  get selected(): TabChainedCurlContainer {
    return this._selected;
  }

  get selectedTabIndex(): number {
    return this._tabSelectedIndex;
  }

  changeTabIndex(_index) {
    console.log("changeTabIndex", _index);
    if (_index >= 0) {
      // console.log(_index, JSON.stringify(this._indexToTabChainedCurlContainer));
      console.log(this._indexToTabChainedCurlContainer[_index].contextId);
      this._tabSelectedIndex = _index;

      for (let [key, value] of this._tabChainedCurls.entries()) {
        console.log(value.assetName, value.contextId, value.index)
        if (value.contextId == this._indexToTabChainedCurlContainer[_index].contextId) {
          this._selected = value;
          return;
        }
      }
    }
  }

  private _contextIdToTabChainedCurlContainer: Map<string, TabChainedCurlContainer> = new Map();
  private _indexToTabChainedCurlContainer: TabChainedCurlContainer[] = [];
  private _tabChainedCurlContainerToindex: Map<string, number> = new Map();
  private _indexes: number[] = [];

  closeTab(contextId: string) {
    // console.log('closeTab', contextId);
    const candidate: TabChainedCurlContainer = this._contextIdToTabChainedCurlContainer.get(contextId);
    const candidateTabIndex = this._tabChainedCurlContainerToindex.get(candidate.contextId);
    this._contextIdToTabChainedCurlContainer.delete(contextId)
    this._indexToTabChainedCurlContainer.splice(candidateTabIndex, 1);
    this._tabChainedCurls.delete(contextId);

    if (this.formMap.has(contextId))
      this.formMap.delete(contextId);
    this._tabCounter--;
    this.shiftLeft(candidateTabIndex)
    this._tabChainedCurls$.next(this._tabChainedCurls);
    // console.log(candidateTabIndex, this._tabSelectedIndex, this._tabCounter)
    if (candidateTabIndex == this._tabSelectedIndex && candidateTabIndex < this._tabCounter) {
      this.changeTabIndex(candidateTabIndex);
    }
  }

  shiftLeft(index) {
    console.log(index, this._indexToTabChainedCurlContainer);
    for (let i = index; i < this._indexToTabChainedCurlContainer.length; i++) {
      // console.log(JSON.stringify(this._tabChainedCurls.get(this._indexToTabChainedCurlContainer[i].contextId)));
      let tabIndex = this._tabChainedCurls.get(this._indexToTabChainedCurlContainer[i].contextId).index - 1;
      this._tabChainedCurls.get(this._indexToTabChainedCurlContainer[i].contextId).index = tabIndex;
      this._tabChainedCurlContainerToindex.set(this._indexToTabChainedCurlContainer[i].contextId, tabIndex);
      // console.log(JSON.stringify(this._tabChainedCurls.get(this._indexToTabChainedCurlContainer[i].contextId)));
    }
  }

  pushNewTab(assetName: string, serverConfig: ChainedCurlServerConfig | any) {
    // this._assetNameToContextId.set(assetName, serverConfig.contextId);
    Object.keys(serverConfig.chainedCurlDto.chain).forEach(key => {
      // console.log("***", key)
      // if (this._chainedCurlDto.chainedCurlDto.chain[key] &&
      //   this._chainedCurlDto.chainedCurlDto.chain[key].provide &&
      //   this._chainedCurlDto.chainedCurlDto.chain[key].provide.input)
      //   this.startStep(key, this._chainedCurlDto.chainedCurlDto.chain[key]);
      this.startStep(serverConfig.contextId, key, serverConfig.chainedCurlDto.chain[key]);

      if (serverConfig.chainedCurlDto.chain[key].provide && serverConfig.chainedCurlDto.chain[key].provide.input) {
        Object.keys(serverConfig.chainedCurlDto.chain[key].provide.input).forEach((inp: string) => {
          // console.log(serverConfig.chainedCurlDto.chain[key].provide)
          if (serverConfig.chainedCurlDto.chain[key].provide.input[inp].type === 'prev-step') {
            console.log(serverConfig.chainedCurlDto.chain[key].provide.input[inp])
            let subject: BehaviorSubject<string> = new BehaviorSubject<string>(null);
            this.collectionObservableService.observe(
              serverConfig.contextId,
              serverConfig.chainedCurlDto.chain[key].provide.input[inp].expression,
              subject);
              subject.subscribe((res: string) => {
                // console.log('sub', res)
                if (res) {
                  let obj: {} = {};
                  obj[inp] = res;
                  this.getFormGroup(serverConfig.contextId, key).patchValue(obj);
                  // console.log(obj, this.getFormGroup(serverConfig.contextId, key).value);
                }
              });
              // this.collectionObservableService.setValue(serverConfig.contextId,
              //   serverConfig.chainedCurlDto.chain[key].provide.input[inp].expression, '12');
          }
        })
      }
        
      // serverConfig.chainedCurlDto.chain[key].provide.input.forEach((value: object, key: string) => {
      //   console.log(key);
      // })
    });

    this._tabChainedCurls = structuredClone(this._tabChainedCurls);
    this._tabSelectedIndex = this._tabCounter++;
    this._indexes.push(this._tabSelectedIndex);
    this._tabChainedCurls.set(serverConfig.contextId, {
      contextId: serverConfig.contextId,
      assetName: assetName,
      index: this._tabSelectedIndex,
      serverConfig: serverConfig
    });

    // console.log("******** ", serverConfig.contextId, assetName);

    this._selected = this._tabChainedCurls.get(serverConfig.contextId);
    this._contextIdToTabChainedCurlContainer.set(serverConfig.contextId, this._selected);
    this._tabChainedCurlContainerToindex.set(this._selected.contextId, this._tabSelectedIndex);
    this._indexToTabChainedCurlContainer[this._tabSelectedIndex] = this._selected;
    this._tabChainedCurls$.next(this._tabChainedCurls);
  }

  private _activeChainedCurl: string = null;
  private _activeChainedCurl$: BehaviorSubject<string> = new BehaviorSubject(this._activeChainedCurl)
  private _chainedCurlList: string[] = null;
  private _chainedCurlList$: BehaviorSubject<string[]> = new BehaviorSubject(this._chainedCurlList);

  get activeChainedCurl$(): Observable<string> {
    return this._activeChainedCurl$.asObservable();
  }

  get chainedCurlList$(): Observable<string[]> {
    return this._chainedCurlList$.asObservable();
  }

  set chainedCurlList(_chainedCurlList: string[] | any) {
    this._chainedCurlList = _chainedCurlList;
    this._chainedCurlList$.next(this._chainedCurlList);
  }

  private _chainedCurlDto: ChainedCurlServerConfig = null;
  private _chainedCurlDto$: BehaviorSubject<ChainedCurlServerConfig> = new BehaviorSubject(this._chainedCurlDto);

  get chainedCurlDto$(): Observable<ChainedCurlServerConfig> {
    return this._chainedCurlDto$.asObservable();
  }

  get chainedCurlDto(): ChainedCurlServerConfig {
    return this._chainedCurlDto;
  }

  // set chainedCurlDto(_chainedCurlDto: ChainedCurlServerConfig | any) {
  //   console.log("active " + this._chainedCurlDto.chainedCurlDto.name)
  //       // console.log(JSON.stringify(this._chainedCurlDto));
  //   // console.log(JSON.stringify(this._chainedCurlDto.chainedCurlDto.chain));
  //   // this._chainedCurlDto.chainedCurlDto.chain.forEach((value: FlowDto, key: string) => {
  //   //   this.startStep(key, value);
  //   // });

  //   console.log("***")
  //   Object.keys(_chainedCurlDto.chainedCurlDto.chain).forEach(key => {
  //     console.log("***", key)
  //     // if (this._chainedCurlDto.chainedCurlDto.chain[key] &&
  //     //   this._chainedCurlDto.chainedCurlDto.chain[key].provide &&
  //     //   this._chainedCurlDto.chainedCurlDto.chain[key].provide.input)
  //     //   this.startStep(key, this._chainedCurlDto.chainedCurlDto.chain[key]);
  //     this.startStep(key, _chainedCurlDto.chainedCurlDto.chain[key]);
  //   });

  // }

  private _chainedCurlResponseMap: Map<string, Map<string, ChainedCurlResponseDto>> = new Map<string, Map<string, ChainedCurlResponseDto>>();
  private _chainedCurlResponseMap$: BehaviorSubject<Map<string, Map<string, ChainedCurlResponseDto>>> = new BehaviorSubject(this._chainedCurlResponseMap);
  private _chainedCurlResponseMapChange: number = 0;
  private _chainedCurlResponseMapChange$: BehaviorSubject<number> = new BehaviorSubject(this._chainedCurlResponseMapChange);

  get chainedCurlResponseMapChanged$(): Observable<number> {
    return this._chainedCurlResponseMapChange$.asObservable();
  }

  get chainedCurlResponseMap$(): Observable<Map<string, Map<string, ChainedCurlResponseDto>>> {
    return this._chainedCurlResponseMap$.asObservable();
  }

  setChainedCurlResponseMap(contextId: string, stepName: string, _chainedCurlResponseDto: ChainedCurlResponseDto) {
    // console.log('***', contextId, stepName, JSON.stringify(this._chainedCurlResponseMap), this._chainedCurlResponseMap.has(contextId), JSON.stringify(_chainedCurlResponseDto))
    if (!this._chainedCurlResponseMap[contextId]) {
      this._chainedCurlResponseMap[contextId] = new Map();
      // console.log('***', JSON.stringify(this._chainedCurlResponseMap));
    }

    this._chainedCurlResponseMap[contextId][stepName] = _chainedCurlResponseDto;
    // console.log('***', JSON.stringify(this._chainedCurlResponseMap));
    // console.log(this._chainedCurlResponseMap)
    // var newMap = {};
    // for (var i in this._chainedCurlResponseMap)
    //   newMap[i] = this._chainedCurlResponseMap[i];
    var newMap = JSON.parse(JSON.stringify(this._chainedCurlResponseMap));
    // console.log('***', JSON.stringify(newMap));
    this._chainedCurlResponseMap$.next(newMap);
    this._chainedCurlResponseMapChange = this._chainedCurlResponseMapChange + 1;
    this._chainedCurlResponseMapChange$.next(this._chainedCurlResponseMapChange)

    let parsed: object = JSON.parse(_chainedCurlResponseDto.bodyAsString);
    Object.keys(parsed).forEach((key: string) => {
      this.collectionObservableService.setValue(contextId, 'res-json#' + stepName + ':' + key, parsed[key]);
    });
  }

  getChainedCurlResponseMap(contextId: string, stepName: string): ChainedCurlResponseDto {
    if (this._chainedCurlResponseMap.has(stepName))
      return this._chainedCurlResponseMap.get(contextId).get(stepName);
    return null;
  }

  // onFileSelected(event: any) {

  //   const file: File = event.target.files[0];

  //   if (file) {
  //     const formData = new FormData();
  //     formData.append("ChainedCurlConfig", file);
  //     this.httpClient.post<ChainedCurlServerConfig>("/api/v1/chained-curl/test/upload-and-get-curl-config", formData, { observe: 'response' }).subscribe(
  //       res => {
  //         this.chainedCurlDto = res.body;
  //       }, err => {

  //       }
  //     );
  //   }
  // }

  asIsOrder(a, b) {
    return 1;
  }

  fetchContext(contextId: string) {
    this.httpClient.get<InitialValuesDto>("/api/v1/chained-curl/test/evaluated-initial-values?context-id=" + contextId, { observe: 'response' })
  }

  startStep(contextId: string, stepName: string, flow: FlowDto) {
    if (this.formMap.has(contextId) && this.formMap.get(contextId).has(stepName)) {
      console.log("step exists!")
      return;
    }
    if (!this.formMap.has(contextId)) {
      let contextFormGroup: Map<string, FormGroup> = new Map<string, FormGroup>();
      this.formMap.set(contextId, contextFormGroup);
    }
    if (!this.formMap.get(contextId).has(stepName)) {
      let group = this.formBuilder.group({
      });

      if (flow &&
        flow.provide &&
        flow.provide.input)
        Object.keys(flow.provide.input).forEach(key => {
          group.addControl(key, new FormControl(''));
        });

      this.formMap.get(contextId).set(stepName, group);
    }
  }

  getFormGroup(contextId: string, stepName: string): FormGroup {
    // console.log(contextId, stepName, this.formMap.get(stepName))
    return this.formMap.get(contextId).get(stepName);
  }

  checkIfRequiredPrevStepValues(contextId: string, stepName: string): boolean {
    this.httpClient.get("/chained-curl/check-if-required-prev-step-values?context-id=" + contextId + "&step-name=" + stepName)
    return false;
  }

  submitForm(contextId: string, stepName: string) {
    console.log(stepName + ' submitted!')
    console.log(this.formMap.get(contextId).get(stepName));
    if (this.formMap.get(stepName))
      console.log(this.formMap.get(contextId).get(stepName).value);

    this.httpClient.post<ChainedCurlResponseDto>("/chained-curl/do-curl?context-id=" + contextId + "&step-name=" + stepName,
      this.formMap.get(contextId).get(stepName).value,
      { observe: 'response' }).subscribe(
        res => {
          console.log(res.body);
          this.setChainedCurlResponseMap(contextId, stepName, res.body);
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

  // stringify2(s): string {
  //   return this.syntaxHighlight(JSON.parse(s))
  // }

  stringify(s): string {
    return JSON.stringify(JSON.parse(s), null, 2)
  }

  stringify3(s): void {
    console.log(JSON.stringify(s))
  }

  confClick(assetName: string) {
    this.httpClient.get("/chained-curl/conf/" + assetName).subscribe(res => {
      this.pushNewTab(assetName, res);
    });
  }

  isProvideOpened(contextId) {
    return this.isStepOpened(contextId, 'provide');
  }

  setProvideOpenStatus(contextId, status: boolean) {
    this.setStepOpenStatus(contextId, 'provide', status);
  }

  isStepOpened(contextId, stepName) {
    // console.log(contextId, stepName);
    if (!this._expandStepMap.has(contextId + '-' + stepName)) {
      this._expandStepMap.set(contextId + '-' + stepName, false);
    }
    return this._expandStepMap.get(contextId + '-' + stepName);
  }

  setStepOpenStatus(contextId, stepName, status: boolean) {
    console.log(contextId, stepName);
    // if (!this._expandStepMap.has(contextId + '-' + stepName)) {
    //   this._expandStepMap.set(contextId + '-' + stepName, status);
    // } else {
    //   this._expandStepMap.set(contextId + '-' + stepName, status);
    // }
    this._expandStepMap.set(contextId + '-' + stepName, status);
  }
}
