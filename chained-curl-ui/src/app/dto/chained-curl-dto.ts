export interface ChainedCurlServerConfig {
    contextId: string
    chainedCurlDto: ChainedCurlDto
    initialValues: InitialValuesDto
    contextMap: Map<string | any, ChainedCurlContextDto>
}

export interface ChainedCurlDto {
    name: string
    show: any
    provide: ProvideDto
    chain: Map<string, FlowDto>
}

export interface ProvideDto {
    supplier: Map<string, string>
    fix: Map<string, string>
    input: Map<string, object>
}

export interface FlowDto {
    show: any
    provide: ProvideDto
    curl: string[]
    continueConditions: string[]
    startConditions: string[];
    extracted: Map<string, string>
}


export interface InitialValuesDto {
    suppliers: object
    fixes: object
    inputs: object
}

export interface ChainedCurlContextDto {
    suppliers: object
    fixes: object
    inputs: object
    extracts: object
}


export interface ChainedCurlResponseDto {
    curlRequest: string
    status: number
    headers: object
    bodyAsString: string
    extracted: object
    showControls: Map<String, object>
}

export interface TabChainedCurlContainer {
    contextId: string
    assetName: string
    index: number
    serverConfig: ChainedCurlServerConfig
}